'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useSession, signOut } from 'next-auth/react';
import api from '@/lib/api';
import {
    Users,
    ArrowRightLeft,
    Bell,
    CreditCard,
    Search,
    Map as MapIcon,
    User,
    LogOut,
    Loader2,
    CheckCircle2,
    Clock,
    XCircle,
    Star
} from 'lucide-react';
import Link from 'next/link';

export default function DashboardPage() {
    const router = useRouter();
    const { data: session } = useSession();
    const [loading, setLoading] = useState(true);
    const [teacher, setTeacher] = useState<any>(null);
    const [matches, setMatches] = useState<any[]>([]);
    const [interests, setInterests] = useState<any[]>([]);
    const [stats, setStats] = useState({
        totalMatches: 0,
        interestsReceived: 0,
        interestsSent: 0
    });

    useEffect(() => {
        const fetchDashboardData = async () => {
            try {
                const [meRes, matchRes, interestRes] = await Promise.all([
                    api.get('/teacher/me'),
                    api.get('/matching/matches'),
                    api.get('/interest/received')
                ]);

                setTeacher(meRes.data.data);
                setMatches(matchRes.data.data || []);
                setInterests(interestRes.data.data || []);

                const sentRes = await api.get('/interest/sent');

                setStats({
                    totalMatches: (matchRes.data.data || []).length,
                    interestsReceived: (interestRes.data.data || []).length,
                    interestsSent: (sentRes.data.data || []).length
                });

            } catch (err: any) {
                if (err.response?.status === 401) {
                    router.push('/login');
                }
            } finally {
                setLoading(false);
            }
        };

        fetchDashboardData();
    }, [router]);

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <Loader2 className="w-12 h-12 text-blue-600 animate-spin" />
            </div>
        );
    }

    const displayName = teacher?.name || session?.user?.name || 'Teacher';
    const userImage = session?.user?.image;
    const isPremium = teacher?.subscriptionStatus > 0;

    return (
        <div className="min-h-screen bg-gray-50 pb-12">
            {/* Top Navigation Bar */}
            <nav className="bg-white shadow-sm border-b sticky top-0 z-10">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between h-16 items-center">
                        <div className="flex items-center gap-2">
                            <div className="bg-blue-600 p-2 rounded-lg">
                                <ArrowRightLeft className="text-white w-6 h-6" />
                            </div>
                            <span className="text-xl font-bold text-gray-900 tracking-tight">TeacherTransfer</span>
                        </div>
                        <div className="flex items-center gap-4">
                            <Link href="/profile" className="p-2 text-gray-500 hover:text-blue-600 transition-colors">
                                {userImage ? (
                                    <img src={userImage} alt="" className="w-8 h-8 rounded-full" />
                                ) : (
                                    <User className="w-6 h-6" />
                                )}
                            </Link>
                            <button
                                onClick={() => signOut({ callbackUrl: '/login' })}
                                className="p-2 text-gray-500 hover:text-red-600 transition-colors"
                            >
                                <LogOut className="w-6 h-6" />
                            </button>
                        </div>
                    </div>
                </div>
            </nav>

            <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-8">
                {/* Welcome & Stats Header */}
                <div className="mb-8">
                    <h1 className="text-3xl font-bold text-gray-900">Welcome back, {displayName.split(' ')[0]}!</h1>
                    <p className="text-gray-500 mt-1">Here is what happening with your transfer requests today.</p>
                </div>

                {/* Stats Grid */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                    <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center gap-4 hover:shadow-md transition-shadow">
                        <div className="bg-blue-50 p-3 rounded-xl text-blue-600">
                            <Users className="w-8 h-8" />
                        </div>
                        <div>
                            <p className="text-sm font-medium text-gray-500 uppercase tracking-wider">Direct Matches</p>
                            <p className="text-3xl font-bold text-gray-900">{stats.totalMatches}</p>
                        </div>
                    </div>
                    <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center gap-4 hover:shadow-md transition-shadow">
                        <div className="bg-green-50 p-3 rounded-xl text-green-600">
                            <Bell className="w-8 h-8" />
                        </div>
                        <div>
                            <p className="text-sm font-medium text-gray-500 uppercase tracking-wider">Interests Received</p>
                            <p className="text-3xl font-bold text-gray-900">{stats.interestsReceived}</p>
                        </div>
                    </div>
                    <div className="bg-white p-6 rounded-2xl shadow-sm border border-gray-100 flex items-center gap-4 hover:shadow-md transition-shadow">
                        <div className="bg-purple-50 p-3 rounded-xl text-purple-600">
                            <Star className="w-8 h-8" />
                        </div>
                        <div>
                            <p className="text-sm font-medium text-gray-500 uppercase tracking-wider">Interests Sent</p>
                            <p className="text-3xl font-bold text-gray-900">{stats.interestsSent}</p>
                        </div>
                    </div>
                </div>

                <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                    {/* Main Content Area */}
                    <div className="lg:col-span-2 space-y-8">
                        {/* Featured Matches Preview */}
                        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                            <div className="p-6 border-b flex justify-between items-center">
                                <h2 className="text-xl font-bold text-gray-900 flex items-center gap-2">
                                    <Clock className="text-blue-600 w-5 h-5" />
                                    Top Direct Matches
                                </h2>
                                <Link href="/matches" className="text-sm font-bold text-blue-600 hover:text-blue-700">
                                    View All
                                </Link>
                            </div>
                            <div className="p-0">
                                {matches.length > 0 ? (
                                    <div className="divide-y">
                                        {matches.slice(0, 3).map((match) => (
                                            <div key={match.teacherId} className="p-6 hover:bg-gray-50 transition-colors flex items-center justify-between group">
                                                <div className="flex items-center gap-4">
                                                    <div className="w-12 h-12 bg-blue-100 text-blue-600 rounded-full flex items-center justify-center font-bold text-xl">
                                                        {match.name[0]}
                                                    </div>
                                                    <div>
                                                        <p className="font-bold text-gray-900 group-hover:text-blue-600 transition-colors">{match.name}</p>
                                                        <p className="text-sm text-gray-500">{match.schoolName} • {match.distanceText}</p>
                                                    </div>
                                                </div>
                                                <div className="flex items-center gap-3">
                                                    <div className="text-right hidden sm:block">
                                                        <div className="text-sm font-bold text-green-600">{match.matchScore}% Match</div>
                                                        <div className="text-xs text-gray-400 capitalize">{match.subject.toLowerCase()}</div>
                                                    </div>
                                                    <button className="bg-blue-50 text-blue-600 p-2 rounded-lg hover:bg-blue-600 hover:text-white transition-all">
                                                        <ArrowRightLeft className="w-5 h-5" />
                                                    </button>
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                ) : (
                                    <div className="p-12 text-center">
                                        <div className="inline-block p-4 bg-gray-50 rounded-full mb-4">
                                            <Search className="w-8 h-8 text-gray-400" />
                                        </div>
                                        <h3 className="text-lg font-bold text-gray-900">No matches yet</h3>
                                        <p className="text-gray-500 mt-1 max-w-xs mx-auto">We couldn't find any direct matches. Try expanding your search radius in settings.</p>
                                        <Link href="/profile" className="mt-4 inline-block text-blue-600 font-bold hover:underline">
                                            Update Preferences
                                        </Link>
                                    </div>
                                )}
                            </div>
                        </div>

                        {/* Recent Interests Card */}
                        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 overflow-hidden">
                            <div className="p-6 border-b">
                                <h2 className="text-xl font-bold text-gray-900">Interests Received</h2>
                            </div>
                            <div className="p-0">
                                {interests.length > 0 ? (
                                    <div className="divide-y">
                                        {interests.slice(0, 3).map((item) => (
                                            <div key={item.id} className="p-6 flex items-center justify-between">
                                                <div className="flex items-center gap-4">
                                                    <div className="w-10 h-10 bg-green-100 text-green-600 rounded-lg flex items-center justify-center">
                                                        <Bell className="w-5 h-5" />
                                                    </div>
                                                    <div>
                                                        <p className="font-bold text-gray-900">{item.senderName} is interested</p>
                                                        <p className="text-sm text-gray-500">{new Date(item.createdAt).toLocaleDateString()}</p>
                                                    </div>
                                                </div>
                                                <div className="flex gap-2">
                                                    <button className="p-2 text-red-500 hover:bg-red-50 rounded-lg transition-colors">
                                                        <XCircle className="w-6 h-6" />
                                                    </button>
                                                    <button className="p-2 text-green-500 hover:bg-green-50 rounded-lg transition-colors">
                                                        <CheckCircle2 className="w-6 h-6" />
                                                    </button>
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                ) : (
                                    <div className="p-8 text-center text-gray-500">
                                        No recent interests received.
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>

                    {/* Right Sidebar Area */}
                    <div className="space-y-6">
                        {!isPremium && (
                            <div className="bg-gradient-to-br from-indigo-600 to-blue-700 rounded-2xl p-6 text-white shadow-xl relative overflow-hidden group">
                                <div className="absolute top-0 right-0 p-4 opacity-10 group-hover:scale-110 transition-transform">
                                    <MapIcon size={120} />
                                </div>
                                <div className="relative z-10">
                                    <div className="bg-white/20 p-2 rounded-lg inline-block mb-4 backdrop-blur-md">
                                        <Star className="text-yellow-300 w-5 h-5" />
                                    </div>
                                    <h3 className="text-xl font-bold mb-2">Unlock Map View</h3>
                                    <p className="text-blue-100 text-sm mb-6 leading-relaxed">See all mutual matches on an interactive map and discover broader transfer opportunities.</p>
                                    <Link href="/subscription" className="block w-full py-3 bg-white text-blue-700 text-center font-bold rounded-xl shadow-lg hover:shadow-white/20 transition-all active:scale-[0.98]">
                                        Upgrade to Premium
                                    </Link>
                                </div>
                            </div>
                        )}

                        <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100">
                            <h3 className="font-bold text-gray-900 mb-4 text-lg">Quick Actions</h3>
                            <div className="grid grid-cols-2 gap-4">
                                <Link href="/matches" className="flex flex-col items-center gap-2 p-4 bg-gray-50 rounded-xl hover:bg-blue-50 transition-colors group">
                                    <Search className="w-6 h-6 text-gray-400 group-hover:text-blue-600" />
                                    <span className="text-xs font-bold text-gray-600 group-hover:text-blue-900">Find Matches</span>
                                </Link>
                                <Link href="/profile" className="flex flex-col items-center gap-2 p-4 bg-gray-50 rounded-xl hover:bg-blue-50 transition-colors group">
                                    <User className="w-6 h-6 text-gray-400 group-hover:text-blue-600" />
                                    <span className="text-xs font-bold text-gray-600 group-hover:text-blue-900">Edit Profile</span>
                                </Link>
                                <Link href="/interests" className="flex flex-col items-center gap-2 p-4 bg-gray-50 rounded-xl hover:bg-blue-50 transition-colors group">
                                    <ArrowRightLeft className="w-6 h-6 text-gray-400 group-hover:text-blue-600" />
                                    <span className="text-xs font-bold text-gray-600 group-hover:text-blue-900">Interests</span>
                                </Link>
                                <Link href="/subscription" className="flex flex-col items-center gap-2 p-4 bg-gray-50 rounded-xl hover:bg-blue-50 transition-colors group">
                                    <CreditCard className="w-6 h-6 text-gray-400 group-hover:text-blue-600" />
                                    <span className="text-xs font-bold text-gray-600 group-hover:text-blue-900">Billing</span>
                                </Link>
                            </div>
                        </div>

                        <div className="bg-blue-50 p-6 rounded-2xl border border-blue-100">
                            <h3 className="font-bold text-blue-900 mb-1">Need help?</h3>
                            <p className="text-sm text-blue-700 mb-4 opacity-80">Our support team is available 24/7 for Bihar teachers.</p>
                            <button className="text-sm font-bold text-blue-600 hover:text-blue-800 transition-colors">
                                Chat with us →
                            </button>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
}
