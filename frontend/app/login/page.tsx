'use client';

import { signIn, getSession } from 'next-auth/react';
import { useState, useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import { ShieldCheck, ArrowRight, Eye, EyeOff, ArrowRightLeft, CheckCircle2 } from 'lucide-react';
import Link from 'next/link';

export default function LoginPage() {
    const router = useRouter();
    const searchParams = useSearchParams();
    const [loading, setLoading] = useState(false);
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [error, setError] = useState('');
    const [registered, setRegistered] = useState(false);

    useEffect(() => {
        if (searchParams.get('registered') === 'true') {
            setRegistered(true);
        }
    }, [searchParams]);

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!email || !password) return;

        setLoading(true);
        setError('');

        const result = await signIn('credentials', {
            email,
            password,
            redirect: false,
        });

        if (result?.error) {
            setError('Invalid email or password');
            setLoading(false);
            return;
        }

        // Successful login — redirect based on session
        const session = await getSession();
        const redirectTo = (session?.user as any)?.redirectTo || '/dashboard';
        router.push(redirectTo);
        router.refresh();
    };

    return (
        <div className="min-h-screen flex flex-col bg-gray-50">
            {/* Main Content */}
            <main className="flex-1 flex items-center justify-center py-12 px-4">
                <div className="w-full max-w-md bg-white rounded-2xl shadow-xl overflow-hidden border border-gray-100">
                    <div className="bg-gradient-to-r from-blue-600 to-indigo-600 p-8 text-white text-center">
                        <div className="flex justify-center mb-4">
                            <div className="p-3 bg-white/20 rounded-full backdrop-blur-sm">
                                <ShieldCheck className="w-8 h-8" />
                            </div>
                        </div>
                        <h1 className="text-2xl font-bold">TeacherTransfer</h1>
                        <p className="mt-1 text-blue-100 text-sm opacity-90">
                            Find your mutual transfer match
                        </p>
                    </div>

                    <div className="p-8">
                        <h2 className="text-xl font-bold text-gray-900 text-center mb-2">
                            Welcome Back
                        </h2>
                        <p className="text-center text-gray-500 text-sm mb-8">
                            Sign in to discover mutual transfer opportunities.
                        </p>

                        {registered && (
                            <div className="bg-green-50 border border-green-200 text-green-700 p-3 rounded-xl text-sm mb-4 flex items-center justify-center gap-2">
                                <CheckCircle2 className="w-5 h-5" />
                                Account created successfully! Please sign in.
                            </div>
                        )}

                        {error && (
                            <div className="bg-red-50 border border-red-200 text-red-700 p-3 rounded-xl text-sm mb-4 text-center">
                                {error}
                            </div>
                        )}

                        <form onSubmit={handleLogin} className="space-y-4">
                            <div>
                                <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
                                    Email address
                                </label>
                                <input
                                    id="email"
                                    type="email"
                                    required
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    placeholder="you@example.com"
                                    className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                                    disabled={loading}
                                />
                            </div>

                            <div>
                                <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">
                                    Password
                                </label>
                                <div className="relative">
                                    <input
                                        id="password"
                                        type={showPassword ? 'text' : 'password'}
                                        required
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                        placeholder="Enter your password"
                                        className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all pr-12"
                                        disabled={loading}
                                    />
                                    <button
                                        type="button"
                                        onClick={() => setShowPassword(!showPassword)}
                                        className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
                                    >
                                        {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                                    </button>
                                </div>
                            </div>

                            <button
                                type="submit"
                                disabled={loading || !email || !password}
                                className="w-full py-3 px-4 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-xl transition-all shadow-sm hover:shadow-md active:scale-[0.98] disabled:opacity-50 flex items-center justify-center gap-2"
                            >
                                {loading ? 'Signing in...' : 'Sign In'}
                                {!loading && <ArrowRight className="w-5 h-5" />}
                            </button>
                        </form>

                        <div className="mt-8 pt-6 border-t border-gray-100 text-center">
                            <p className="text-sm text-gray-500">
                                Don't have an account?{' '}
                                <Link href="/auth/signup" className="text-blue-600 hover:text-blue-700 font-bold">
                                    Sign up
                                </Link>
                            </p>
                        </div>
                    </div>
                </div>
            </main>

            {/* Footer */}
            <footer className="bg-white border-t border-gray-200 py-12 px-4 sm:px-6 lg:px-8">
                <div className="max-w-7xl mx-auto flex flex-col md:flex-row justify-between items-center gap-6">
                    <div className="flex items-center gap-2">
                        <ArrowRightLeft className="text-blue-600 w-5 h-5" />
                        <span className="font-bold text-gray-900">TeacherTransfer</span>
                    </div>
                    <div className="flex gap-6 text-sm font-medium text-gray-500">
                        <Link href="#" className="hover:text-blue-600">About Us</Link>
                        <Link href="#" className="hover:text-blue-600">FAQ</Link>
                        <Link href="#" className="hover:text-blue-600">Privacy Policy</Link>
                        <Link href="#" className="hover:text-blue-600">Terms of Service</Link>
                    </div>
                    <div className="text-sm text-gray-400">
                        &copy; {new Date().getFullYear()} TeacherTransfer. All rights reserved.
                    </div>
                </div>
            </footer>
        </div>
    );
}
