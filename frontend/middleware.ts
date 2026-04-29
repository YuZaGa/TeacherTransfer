import { withAuth } from 'next-auth/middleware';
import { NextResponse } from 'next/server';

export default withAuth(
    function middleware(req) {
        const { pathname } = req.nextUrl;
        const token = req.nextauth.token;

        // Logged in but not onboarded — force to onboarding
        if (token && !token.isOnboarded && pathname !== '/onboarding') {
            return NextResponse.redirect(new URL('/onboarding', req.url));
        }

        // Logged in and onboarded — prevent going back to onboarding/login
        if (token && token.isOnboarded && (pathname === '/onboarding' || pathname === '/login' || pathname === '/auth/otp')) {
            return NextResponse.redirect(new URL('/dashboard', req.url));
        }

        return NextResponse.next();
    },
    {
        pages: {
            signIn: '/login',
        },
        callbacks: {
            authorized: ({ token, req }) => {
                const { pathname } = req.nextUrl;
                // Public routes that don't require auth
                const publicRoutes = ['/', '/login', '/auth/otp'];
                if (publicRoutes.includes(pathname)) return true;
                // All other routes require authentication
                return !!token;
            },
        },
    }
);

export const config = {
    matcher: ['/((?!_next/static|_next/image|favicon.ico|api).*)'],
};
