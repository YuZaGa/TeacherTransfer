import { withAuth } from 'next-auth/middleware';
import { NextResponse } from 'next/server';

export default withAuth(
    function middleware(req) {
        const { pathname } = req.nextUrl;
        const token = req.nextauth.token;
        const isLoggedIn = !!token;
        const isOnboarded = token?.isOnboarded ?? false;

        // Public routes that don't require auth
        const publicRoutes = ['/login', '/auth/otp', '/'];
        const isPublicRoute = publicRoutes.includes(pathname);

        // Logged in but not onboarded — force to onboarding
        if (isLoggedIn && !isOnboarded && pathname !== '/onboarding') {
            return NextResponse.redirect(new URL('/onboarding', req.url));
        }

        // Logged in and onboarded — prevent going back to onboarding/login
        if (isLoggedIn && isOnboarded && (pathname === '/onboarding' || pathname === '/login' || pathname === '/auth/otp')) {
            return NextResponse.redirect(new URL('/dashboard', req.url));
        }

        return NextResponse.next();
    },
    {
        pages: {
            signIn: '/login',
        },
    }
);

export const config = {
    matcher: ['/((?!_next/static|_next/image|favicon.ico|api).*)'],
};
