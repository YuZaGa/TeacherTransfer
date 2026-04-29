import NextAuth, { type NextAuthOptions } from 'next-auth';
import CredentialsProvider from 'next-auth/providers/credentials';

const API_URL = process.env.BACKEND_API_URL || process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

export const authOptions: NextAuthOptions = {
    providers: [
        CredentialsProvider({
            name: 'Email & Password',
            credentials: {
                email: { label: 'Email', type: 'email' },
                password: { label: 'Password', type: 'password' },
            },
            async authorize(credentials) {
                if (!credentials?.email || !credentials?.password) {
                    return null;
                }

                try {
                    const res = await fetch(`${API_URL}/auth/login`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({
                            email: credentials.email,
                            password: credentials.password,
                        }),
                    });

                    if (!res.ok) {
                        console.error('[Auth] Backend login failed:', res.status);
                        return null;
                    }

                    const json = await res.json();

                    if (!json.success || !json.data) {
                        console.error('[Auth] Backend returned error:', json.message);
                        return null;
                    }

                    const { data } = json;

                    return {
                        id: String(data.teacherId),
                        name: data.name,
                        email: data.email,
                        image: data.profilePictureUrl || null,
                        isOnboarded: true,
                        backendToken: data.token,
                    };
                } catch (error) {
                    console.error('[Auth] authorize error:', error);
                    return null;
                }
            },
        }),
    ],
    session: {
        strategy: 'jwt',
    },
    pages: {
        signIn: '/login',
    },
    callbacks: {
        async jwt({ token, user }) {
            if (user) {
                token.id = user.id;
                token.isOnboarded = (user as any).isOnboarded ?? false;
                token.backendToken = (user as any).backendToken ?? null;
            }
            return token;
        },
        async session({ session, token }) {
            if (session.user) {
                (session.user as any).id = token.id;
                (session.user as any).isOnboarded = token.isOnboarded;
                (session.user as any).backendToken = token.backendToken;
            }
            return session;
        },
    },
    secret: process.env.NEXTAUTH_SECRET,
};

export default NextAuth(authOptions);
