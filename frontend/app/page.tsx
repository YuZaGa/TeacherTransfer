import Link from 'next/link';
import { ArrowRight, UserPlus, Sliders, CheckCircle, ArrowRightLeft, Map } from 'lucide-react';

export default function Home() {
    return (
        <div className="min-h-screen bg-gray-50 flex flex-col font-sans">
            <main className="flex-1">
                {/* Hero Section */}
                <section className="bg-gradient-to-br from-blue-50 to-indigo-50 py-20 px-4 sm:px-6 lg:px-8 relative overflow-hidden">
                    <div className="absolute top-0 right-0 p-12 opacity-5 hidden lg:block">
                        <Map size={400} />
                    </div>
                    <div className="max-w-3xl mx-auto text-center relative z-10">
                        <h1 className="text-5xl sm:text-6xl font-black text-gray-900 tracking-tight mb-6 leading-tight">
                            Find Your Perfect <br className="hidden sm:block" />
                            <span className="text-blue-600">School Transfer</span>
                        </h1>
                        <p className="text-xl text-gray-600 mb-10 max-w-2xl mx-auto leading-relaxed">
                            A dedicated platform helping Bihar government school teachers discover mutual transfer opportunities effortlessly. Connect, collaborate, and relocate.
                        </p>
                        <div className="flex flex-col sm:flex-row items-center justify-center gap-4">
                            <Link href="/login" className="w-full sm:w-auto px-8 py-4 bg-blue-600 text-white font-bold text-lg rounded-xl shadow-lg hover:shadow-xl hover:bg-blue-700 hover:-translate-y-0.5 transition-all flex items-center justify-center gap-2">
                                Get Started Now
                                <ArrowRight className="w-5 h-5" />
                            </Link>
                        </div>
                    </div>
                </section>

                {/* How It Works */}
                <section className="py-20 px-4 sm:px-6 lg:px-8 bg-white">
                    <div className="max-w-7xl mx-auto">
                        <div className="text-center mb-16">
                            <h2 className="text-3xl font-black text-gray-900 mb-4">How It Works</h2>
                            <p className="text-gray-500 text-lg">Three simple steps to your new posting.</p>
                        </div>

                        <div className="grid grid-cols-1 md:grid-cols-3 gap-12 max-w-5xl mx-auto relative">
                            {/* Connecting Line */}
                            <div className="hidden md:block absolute top-10 left-[16%] right-[16%] h-0.5 bg-gray-100 z-0"></div>

                            <div className="relative z-10 flex flex-col items-center text-center">
                                <div className="w-20 h-20 bg-blue-50 rounded-2xl flex items-center justify-center mb-6 shadow-sm border border-blue-100">
                                    <UserPlus className="w-10 h-10 text-blue-600" />
                                </div>
                                <h3 className="text-xl font-bold text-gray-900 mb-2">1. Sign In</h3>
                                <p className="text-gray-600">Sign in with your email and fill in your teacher details to get started.</p>
                            </div>

                            <div className="relative z-10 flex flex-col items-center text-center">
                                <div className="w-20 h-20 bg-blue-50 rounded-2xl flex items-center justify-center mb-6 shadow-sm border border-blue-100">
                                    <Sliders className="w-10 h-10 text-blue-600" />
                                </div>
                                <h3 className="text-xl font-bold text-gray-900 mb-2">2. Set Preferences</h3>
                                <p className="text-gray-600">Define your target district, block, and set a search radius for your preferred destination.</p>
                            </div>

                            <div className="relative z-10 flex flex-col items-center text-center">
                                <div className="w-20 h-20 bg-green-50 rounded-2xl flex items-center justify-center mb-6 shadow-sm border border-green-100">
                                    <CheckCircle className="w-10 h-10 text-green-600" />
                                </div>
                                <h3 className="text-xl font-bold text-gray-900 mb-2">3. Match</h3>
                                <p className="text-gray-600">Discover compatible teachers, send mutual interests, and connect when both agree.</p>
                            </div>
                        </div>
                    </div>
                </section>

                {/* Features Highlights */}
                <section className="bg-gray-50 py-20 px-4 sm:px-6 lg:px-8 border-t border-gray-100 border-b">
                    <div className="max-w-4xl mx-auto text-center">
                        <ArrowRightLeft className="w-16 h-16 text-blue-600 mx-auto mb-6 opacity-80" />
                        <h2 className="text-4xl font-black text-gray-900 mb-6 tracking-tight">Focus on Mutual Transfers</h2>
                        <p className="text-xl text-gray-600 leading-relaxed max-w-2xl mx-auto">
                            Our matching algorithm is laser-focused on finding direct 1-to-1 swaps. We connect you quickly with teachers who want to move to your current location, and whose current location matches your preferences. Simple, direct, and effective.
                        </p>
                    </div>
                </section>
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