import { NextResponse } from 'next/server';
import { getToken } from 'next-auth/jwt';
import type { NextRequest } from 'next/server';

export async function middleware(request: NextRequest) {
    const { pathname } = request.nextUrl;

    // Public routes that don't require auth
    const publicRoutes = ['/', '/login', '/auth/otp'];
    if (publicRoutes.includes(pathname)) {
        return NextResponse.next();
    }

    // Get the JWT token
    const token = await getToken({
        req: request,
        secret: process.env.NEXTAUTH_SECRET,
    });

    // All other routes require authentication
    if (!token) {
        return NextResponse.redirect(new URL('/login', request.url));
    }

    // Logged in but not onboarded — force to onboarding
    const isOnboarded = (token as any)?.isOnboarded ?? false;
    if (!isOnboarded && pathname !== '/onboarding') {
        return NextResponse.redirect(new URL('/onboarding', request.url));
    }

    // Logged in and onboarded — prevent going back to onboarding/login
    if (isOnboarded && (pathname === '/onboarding' || pathname === '/login' || pathname === '/auth/otp')) {
        return NextResponse.redirect(new URL('/dashboard', request.url));
    }

    return NextResponse.next();
}

export const config = {
    matcher: ['/((?!_next/static|_next/image|favicon.ico|api).*)'],
};
