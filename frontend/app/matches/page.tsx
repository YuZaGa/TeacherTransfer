'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import api from '@/lib/api';
import {
    Search,
    Filter,
    MapPin,
    ArrowRightLeft,
    Loader2,
    Heart,
    ChevronRight,
    ArrowLeft,
    Check
} from 'lucide-react';
import Link from 'next/link';

export default function MatchesPage() {
    const router = useRouter();
    const [loading, setLoading] = useState(true);
    const [matches, setMatches] = useState<any[]>([]);
    const [tab, setTab] = useState<'mutual' | 'all'>('mutual');
    const [filter, setFilter] = useState({
        districtId: '',
        subject: '',
        schoolType: ''
    });
    const [sendingInterest, setSendingInterest] = useState<number | null>(null);
    const [sentInterests, setSentInterests] = useState<Set<number>>(new Set());

    useEffect(() => {
        const fetchMatches = async () => {
            try {
                const response = await api.get('/matching/matches');
                setMatches(response.data.data || []);

                // Fetch sent interests to mark already sent ones
                const interestRes = await api.get('/interest/sent');
                const sentIds = new Set<number>(interestRes.data.data.map((i: any) => i.receiverId));
                setSentInterests(sentIds);
            } catch (err: any) {
                if (err.response?.status === 401) {
                    router.push('/login');
                }
            } finally {
                setLoading(false);
            }
        };

        fetchMatches();
    }, [router]);

    const handleSendInterest = async (matchId: number) => {
        setSendingInterest(matchId);
        try {
            await api.post('/interest/send', { receiverId: matchId });
            setSentInterests(new Set([...Array.from(sentInterests), matchId]));
        } catch (err) {
            console.error('Failed to send interest', err);
        } finally {
            setSendingInterest(null);
        }
    };

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <Loader2 className="w-12 h-12 text-blue-600 animate-spin" />
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 pb-20">
            {/* Header */}
            <header className="bg-white border-b sticky top-0 z-10">
                <div className="max-w-5xl mx-auto px-4 py-4 flex items-center justify-between">
                    <div className="flex items-center gap-4">
                        <Link href="/dashboard" className="p-2 hover:bg-gray-100 rounded-full transition-colors">
                            <ArrowLeft className="w-6 h-6 text-gray-600" />
                        </Link>
                        <h1 className="text-2xl font-bold text-gray-900">Direct Matches</h1>
                    </div>
                </div>
            </header>

            <main className="max-w-5xl mx-auto px-4 pt-8">
                {/* Search & Filter Bar */}
                <div className="bg-white p-4 rounded-2xl shadow-sm border border-gray-100 mb-8 flex flex-col md:flex-row gap-4 items-center">
                    <div className="relative flex-1 w-full">
                        <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-gray-400 w-5 h-5" />
                        <input
                            type="text"
                            placeholder="Search by school or block name..."
                            className="w-full pl-12 pr-4 py-3 bg-gray-50 border-none rounded-xl focus:ring-2 focus:ring-blue-500 outline-none"
                        />
                    </div>
                    <button className="flex items-center gap-2 px-6 py-3 bg-gray-50 text-gray-600 rounded-xl hover:bg-gray-100 transition-colors font-bold">
                        <Filter className="w-5 h-5" />
                        Filters
                    </button>
                    <Link href="/matches/map" className="flex items-center gap-2 px-6 py-3 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-all font-bold shadow-lg shadow-blue-200">
                        <MapPin className="w-5 h-5" />
                        Map View
                    </Link>
                </div>

                {/* Tabs */}
                <div className="flex p-1 bg-gray-200 rounded-2xl mb-8 w-fit mx-auto sm:mx-0">
                    <button
                        onClick={() => setTab('mutual')}
                        className={`px-8 py-3 rounded-xl font-black text-sm transition-all ${tab === 'mutual' ? 'bg-white text-blue-600 shadow-sm' : 'text-gray-500 hover:text-gray-700'}`}
                    >
                        Mutual Matches
                    </button>
                    <button
                        onClick={() => setTab('all')}
                        className={`px-8 py-3 rounded-xl font-black text-sm transition-all ${tab === 'all' ? 'bg-white text-blue-600 shadow-sm' : 'text-gray-500 hover:text-gray-700'}`}
                    >
                        All Potential
                    </button>
                </div>

                {/* Matches List */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    {matches.map((match) => (
                        <div key={match.teacherId} className="bg-white rounded-3xl p-6 shadow-sm border border-gray-100 hover:shadow-xl transition-all group overflow-hidden relative">
                            {/* Match Score Badge */}
                            <div className="absolute top-0 right-0 py-2 px-4 bg-green-50 text-green-600 rounded-bl-2xl font-black text-sm">
                                {match.matchScore}% Match
                            </div>

                            <div className="flex items-start gap-4 mb-6">
                                <div className="w-16 h-16 bg-gradient-to-br from-blue-100 to-indigo-100 text-blue-600 rounded-2xl flex items-center justify-center font-black text-2xl shadow-inner">
                                    {match.name[0]}
                                </div>
                                <div className="pt-1">
                                    <h3 className="text-xl font-black text-gray-900 leading-tight">{match.name}</h3>
                                    <p className="text-blue-600 font-bold text-sm uppercase tracking-wide">{match.subject}</p>
                                </div>
                            </div>

                            <div className="space-y-3 mb-8">
                                <div className="flex items-center gap-3 text-gray-600 bg-gray-50 p-3 rounded-xl">
                                    <MapPin className="w-5 h-5 text-blue-400 flex-shrink-0" />
                                    <span className="text-sm font-semibold truncate">{match.schoolName}</span>
                                </div>
                                <div className="flex items-center justify-between px-2">
                                    <span className="text-sm font-bold text-gray-400">Distance from you</span>
                                    <span className="text-sm font-black text-gray-900">{match.distanceText}</span>
                                </div>
                            </div>

                            <div className="flex items-center gap-3">
                                <button
                                    onClick={() => handleSendInterest(match.teacherId)}
                                    disabled={sentInterests.has(match.teacherId)}
                                    className={`flex-1 py-4 px-6 rounded-2xl font-black text-lg transition-all flex items-center justify-center gap-2 ${sentInterests.has(match.teacherId)
                                        ? 'bg-green-100 text-green-600 cursor-default'
                                        : 'bg-blue-600 text-white hover:bg-blue-700 shadow-lg shadow-blue-100 active:scale-95'
                                        }`}
                                >
                                    {sendingInterest === match.teacherId ? (
                                        <Loader2 className="w-6 h-6 animate-spin" />
                                    ) : sentInterests.has(match.teacherId) ? (
                                        <>
                                            <Check className="w-6 h-6" />
                                            Interest Sent
                                        </>
                                    ) : (
                                        <>
                                            <Heart className="w-6 h-6" />
                                            Express Interest
                                        </>
                                    )}
                                </button>
                                <button className="p-4 bg-gray-50 text-gray-400 rounded-2xl hover:bg-gray-100 transition-colors">
                                    <ChevronRight className="w-6 h-6" />
                                </button>
                            </div>
                        </div>
                    ))}

                    {matches.length === 0 && (
                        <div className="col-span-full py-20 text-center bg-white rounded-3xl border border-dashed border-gray-200">
                            <div className="w-20 h-20 bg-gray-50 rounded-full flex items-center justify-center mx-auto mb-6">
                                <Search className="w-10 h-10 text-gray-300" />
                            </div>
                            <h3 className="text-2xl font-bold text-gray-900">Finding your ideal transfer...</h3>
                            <p className="text-gray-500 mt-2 max-w-sm mx-auto font-medium">We're constantly updating our database. Check back soon or try expanding your preference area!</p>
                        </div>
                    )}
                </div>
            </main>
        </div>
    );
}
