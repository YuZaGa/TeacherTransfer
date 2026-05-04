'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { Mail, Lock, KeyRound, ArrowRight, ArrowRightLeft, CheckCircle2, Loader2 } from 'lucide-react';

const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

export default function ForgotPasswordPage() {
    const router = useRouter();
    const [step, setStep] = useState<'email' | 'otp'>('email');
    const [email, setEmail] = useState('');
    const [otp, setOtp] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');

    const handleSendOtp = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!email) return;

        setLoading(true);
        setError('');

        try {
            const res = await fetch(`${API_URL}/auth/password-reset/send-otp`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email }),
            });

            const json = await res.json();

            if (!json.success) {
                setError(json.message || 'Failed to send OTP');
                return;
            }

            setStep('otp');
            setSuccess('OTP sent to your email');
        } catch {
            setError('Network error. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const handleResetPassword = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!otp || !newPassword) return;

        if (newPassword.length < 8) {
            setError('Password must be at least 8 characters');
            return;
        }

        setLoading(true);
        setError('');

        try {
            const res = await fetch(`${API_URL}/auth/password-reset/reset`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, otp, newPassword }),
            });

            const json = await res.json();

            if (!json.success) {
                setError(json.message || 'Password reset failed');
                return;
            }

            router.push('/login');
            router.refresh();
        } catch {
            setError('Network error. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex flex-col bg-gray-50">

            {/* Main Content */}
            <main className="flex-1 flex items-center justify-center py-12 px-4">
                <div className="w-full max-w-md bg-white rounded-2xl shadow-xl overflow-hidden border border-gray-100">
                    <div className="bg-gradient-to-r from-blue-600 to-indigo-600 p-8 text-white text-center">
                        <div className="flex justify-center mb-4">
                            <div className="p-3 bg-white/20 rounded-full backdrop-blur-sm">
                                {step === 'email' ? <Mail className="w-8 h-8" /> : <KeyRound className="w-8 h-8" />}
                            </div>
                        </div>
                        <h1 className="text-2xl font-bold">TeacherTransfer</h1>
                        <p className="mt-1 text-blue-100 text-sm opacity-90">
                            {step === 'email' ? 'Reset your password' : 'Set new password'}
                        </p>
                    </div>

                    <div className="p-8">
                        {/* Step indicator */}
                        <div className="flex items-center justify-center gap-2 mb-8">
                            <div className={`w-8 h-8 rounded-full flex items-center justify-center text-xs font-bold ${
                                step === 'email' ? 'bg-blue-600 text-white' : 'bg-blue-100 text-blue-600'
                            }`}>1</div>
                            <div className={`h-0.5 w-12 ${step === 'otp' ? 'bg-blue-600' : 'bg-gray-200'}`} />
                            <div className={`w-8 h-8 rounded-full flex items-center justify-center text-xs font-bold ${
                                step === 'otp' ? 'bg-blue-600 text-white' : 'bg-gray-200 text-gray-400'
                            }`}>2</div>
                        </div>

                        {error && (
                            <div className="bg-red-50 border border-red-200 text-red-700 p-3 rounded-xl text-sm mb-4 text-center">
                                {error}
                            </div>
                        )}

                        {success && (
                            <div className="bg-green-50 border border-green-200 text-green-700 p-3 rounded-xl text-sm mb-4 flex items-center justify-center gap-2">
                                <CheckCircle2 className="w-4 h-4" />
                                {success}
                            </div>
                        )}

                        {step === 'email' ? (
                            <form onSubmit={handleSendOtp} className="space-y-4">
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

                                <button
                                    type="submit"
                                    disabled={loading || !email}
                                    className="w-full py-3 px-4 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-xl transition-all shadow-sm hover:shadow-md active:scale-[0.98] disabled:opacity-50 flex items-center justify-center gap-2"
                                >
                                    {loading ? (
                                        <>
                                            <Loader2 className="w-5 h-5 animate-spin" />
                                            Sending OTP...
                                        </>
                                    ) : (
                                        <>
                                            Send OTP
                                            <ArrowRight className="w-5 h-5" />
                                        </>
                                    )}
                                </button>
                            </form>
                        ) : (
                            <form onSubmit={handleResetPassword} className="space-y-4">
                                <div className="bg-blue-50 rounded-xl p-3 text-center">
                                    <p className="text-sm text-blue-700 font-medium">OTP sent to</p>
                                    <p className="text-sm text-blue-800 font-bold">{email}</p>
                                </div>

                                <div>
                                    <label htmlFor="otp" className="block text-sm font-medium text-gray-700 mb-1">
                                        OTP
                                    </label>
                                    <input
                                        id="otp"
                                        type="text"
                                        required
                                        maxLength={6}
                                        value={otp}
                                        onChange={(e) => setOtp(e.target.value.replace(/\D/g, '').slice(0, 6))}
                                        placeholder="Enter 6-digit OTP"
                                        className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all text-center text-lg font-bold tracking-[8px]"
                                        disabled={loading}
                                    />
                                </div>

                                <div>
                                    <label htmlFor="newPassword" className="block text-sm font-medium text-gray-700 mb-1">
                                        New Password
                                    </label>
                                    <input
                                        id="newPassword"
                                        type="password"
                                        required
                                        minLength={8}
                                        value={newPassword}
                                        onChange={(e) => setNewPassword(e.target.value)}
                                        placeholder="At least 8 characters"
                                        className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                                        disabled={loading}
                                    />
                                    <p className="text-xs text-gray-400 mt-1">Minimum 8 characters</p>
                                </div>

                                <button
                                    type="submit"
                                    disabled={loading || !otp || !newPassword || newPassword.length < 8}
                                    className="w-full py-3 px-4 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-xl transition-all shadow-sm hover:shadow-md active:scale-[0.98] disabled:opacity-50 flex items-center justify-center gap-2"
                                >
                                    {loading ? (
                                        <>
                                            <Loader2 className="w-5 h-5 animate-spin" />
                                            Resetting password...
                                        </>
                                    ) : (
                                        <>
                                            Reset Password
                                            <ArrowRight className="w-5 h-5" />
                                        </>
                                    )}
                                </button>

                                <button
                                    type="button"
                                    onClick={() => { setStep('email'); setOtp(''); setNewPassword(''); setError(''); setSuccess(''); }}
                                    className="w-full text-sm text-gray-500 hover:text-blue-600 transition-colors pt-2"
                                >
                                    Change email
                                </button>
                            </form>
                        )}

                        <div className="mt-8 pt-6 border-t border-gray-100 text-center">
                            <p className="text-sm text-gray-500">
                                Remember your password?{' '}
                                <Link href="/login" className="text-blue-600 hover:text-blue-700 font-bold">
                                    Sign in
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
                    <div className="text-sm text-gray-400">
                        &copy; {new Date().getFullYear()} TeacherTransfer. All rights reserved.
                    </div>
                </div>
            </footer>
        </div>
    );
}
