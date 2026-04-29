'use client';

import { signIn } from 'next-auth/react';
import { useEffect } from 'react';

export default function AuthPage() {
    useEffect(() => {
        // Auto-redirect to Google sign-in
        signIn('google', { callbackUrl: '/dashboard' });
    }, []);

    return (
        <div className="flex items-center justify-center min-h-screen">
            <p className="text-gray-500">Redirecting to Google Sign-In...</p>
        </div>
    );
}
