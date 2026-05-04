'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import api from '@/lib/api';
import {
    Search,
    MapPin,
    Loader2,
    Heart,
    CheckCircle2,
    ShieldCheck,
    Shield,
    Lightbulb
} from 'lucide-react';
import Link from 'next/link';

type TabType = 'discovery' | 'mutual';

export default function MatchesPage() {
    const router = useRouter();
    const [loading, setLoading] = useState(true);
    const [tab, setTab] = useState<TabType>('discovery');
    const [discoveryMatches, setDiscoveryMatches] = useState<any[]>([]);
    const [mutualMatches, setMutualMatches] = useState<any[]>([]);
    const [sendingInterest, setSendingInterest] = useState<number | null>(null);
    const [sentInterestIds, setSentInterestIds] = useState<Set<number>>(new Set());
    const [teacher, setTeacher] = useState<any>(null);

    useEffect(() => {
        const fetchAll = async () => {
            try {
                const [meRes, discRes, mutRes, sntInterestRes] = await Promise.all([
                    api.get('/teacher/me'),
                    api.get('/matches'),
                    api.get('/matches/mutual'),
                    api.get('/interest/sent')
                ]);

                setTeacher(meRes.data.data);
                setDiscoveryMatches(discRes.data.data || []);
                setMutualMatches(mutRes.data.data || []);

                const sentIds = new Set<number>(
                    (sntInterestRes.data.data || [])
                        .filter((i: any) => i.status === 'PENDING' || i.status === 'ACCEPTED')
                        .map((i: any) => i.toTeacherId)
                );
                setSentInterestIds(sentIds);
            } catch (err: any) {
                if (err.response?.status === 401) {
                    router.push('/login');
                }
            } finally {
                setLoading(false);
            }
        };

        fetchAll();
    }, [router]);

    const handleSendInterest = async (teacherId: number) => {
        setSendingInterest(teacherId);
        try {
            await api.post(`/interest/${teacherId}`);
            setSentInterestIds(prev => new Set(prev).add(teacherId));
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

    const currentMatches = tab === 'discovery' ? discoveryMatches : mutualMatches;

    const preferredArea = teacher?.preferredLocation?.blockName
        || teacher?.preferredLocation?.districtName
        || 'your area';

    return (
        <div className="min-h-screen bg-gray-50 pb-20">
            <main className="max-w-5xl mx-auto px-4 pt-6">
                <div className="flex p-1 bg-gray-200 rounded-2xl mb-4 w-fit mx-auto sm:mx-0">
                    <button
                        onClick={() => setTab('discovery')}
                        className={`px-5 py-3 rounded-xl font-bold text-sm transition-all flex items-center gap-1.5 ${tab === 'discovery' ? 'bg-white text-blue-600 shadow-sm' : 'text-gray-500 hover:text-gray-700'}`}
                    >
                        <MapPin className="w-4 h-4" />
                        Nearby Teachers ({discoveryMatches.length})
                    </button>
                    <button
                        onClick={() => setTab('mutual')}
                        className={`px-5 py-3 rounded-xl font-bold text-sm transition-all flex items-center gap-1.5 ${tab === 'mutual' ? 'bg-white text-green-600 shadow-sm' : 'text-gray-500 hover:text-gray-700'}`}
                    >
                        <ShieldCheck className="w-4 h-4" />
                        Mutual Matches ({mutualMatches.length})
                    </button>
                </div>

                {tab === 'discovery' && (
                    <div className="mb-6 bg-blue-50 border border-blue-100 rounded-xl px-4 py-3 text-sm text-blue-700">
                        Showing teachers currently posted near your preferred location ({preferredArea}).
                        Send interest to connect — contact details unlock on mutual match.
                    </div>
                )}

                {tab === 'mutual' && mutualMatches.length > 0 && (
                    <div className="mb-6 bg-green-50 border border-green-100 rounded-xl px-4 py-3 text-sm text-green-700">
                        These teachers want a transfer near you, and you want a transfer near them. Contact details are unlocked.
                    </div>
                )}

                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {currentMatches.map((match: any) => {
                        const t = match.teacher || {};
                        const teacherId = t.id;
                        const distanceKm = t.distanceKm || match.distanceKm;
                        const isMutual = tab === 'mutual';
                        const isSent = sentInterestIds.has(teacherId);

                        return (
                            <div key={match.id || teacherId} className={`rounded-2xl p-5 shadow-sm border transition-all ${
                                isMutual
                                    ? 'bg-white border-green-200 bg-gradient-to-br from-white to-green-50/30'
                                    : 'bg-white border-gray-100 hover:shadow-md'
                            }`}>
                                {isMutual && (
                                    <div className="flex items-center gap-1.5 mb-3">
                                        <ShieldCheck className="w-4 h-4 text-green-600" />
                                        <span className="text-xs font-bold text-green-600 uppercase tracking-wider">Mutual Match</span>
                                    </div>
                                )}

                                <div className="flex items-start justify-between mb-3">
                                    <div>
                                        <p className="text-lg font-bold text-gray-900">
                                            {Math.round(distanceKm)} km away
                                        </p>
                                        <p className="text-sm text-gray-500">{t.approxArea || 'Nearby'}</p>
                                    </div>
                                    <span className="text-xs font-bold px-2 py-1 bg-blue-50 text-blue-600 rounded-full">
                                        {t.schoolType}
                                    </span>
                                </div>

                                <div className="space-y-1.5 mb-4">
                                    <div className="flex justify-between text-sm">
                                        <span className="text-gray-500">Subject</span>
                                        <span className="font-semibold text-gray-700">{t.subject}</span>
                                    </div>
                                    <div className="flex justify-between text-sm">
                                        <span className="text-gray-500">School Type</span>
                                        <span className="font-semibold text-gray-700">{t.schoolType}</span>
                                    </div>
                                </div>

                                {!isMutual && (
                                    <div className="flex items-center gap-1 text-xs text-gray-400 mb-4">
                                        <Shield className="w-3 h-3" />
                                        Identity hidden until mutual match
                                    </div>
                                )}

                                {isMutual && t.identityRevealed && (
                                    <div className="bg-green-50 rounded-xl p-4 mb-4 space-y-2">
                                        <p className="font-bold text-green-800">{t.name}</p>
                                        <p className="text-sm text-green-700">{t.schoolName}</p>
                                        {t.phone && (
                                            <p className="text-sm font-semibold text-green-800">{t.phone}</p>
                                        )}
                                    </div>
                                )}

                                {!isMutual && (
                                    <div className="flex gap-2">
                                        <button
                                            onClick={() => handleSendInterest(teacherId)}
                                            disabled={isSent || sendingInterest === teacherId}
                                            className={`flex-1 py-3 px-4 rounded-xl font-bold text-sm flex items-center justify-center gap-1.5 transition-all ${
                                                isSent
                                                    ? 'bg-green-50 text-green-600 cursor-default'
                                                    : 'bg-blue-600 text-white hover:bg-blue-700 active:scale-95'
                                            }`}
                                        >
                                            {sendingInterest === teacherId ? (
                                                <Loader2 className="w-5 h-5 animate-spin" />
                                            ) : isSent ? (
                                                <>
                                                    <CheckCircle2 className="w-5 h-5" />
                                                    Interest Sent
                                                </>
                                            ) : (
                                                <>
                                                    <Heart className="w-5 h-5" />
                                                    Interested
                                                </>
                                            )}
                                        </button>
                                    </div>
                                )}
                            </div>
                        );
                    })}

                    {currentMatches.length === 0 && (
                        <div className="col-span-full py-20 text-center bg-white rounded-2xl border border-dashed border-gray-200">
                            <div className="w-20 h-20 bg-gray-50 rounded-full flex items-center justify-center mx-auto mb-6">
                                <Search className="w-10 h-10 text-gray-300" />
                            </div>
                            <h3 className="text-2xl font-bold text-gray-900">
                                {tab === 'discovery' ? 'No teachers found nearby' :
                                 tab === 'mutual' ? 'No mutual matches yet' :
                                 'No interests sent yet'}
                            </h3>
                            <p className="text-gray-500 mt-2 max-w-md mx-auto font-medium">
                                {tab === 'discovery'
                                    ? `Start by browsing teachers posted near ${preferredArea}. Express interest to unlock mutual matches.`
                                    : tab === 'mutual'
                                    ? 'Express interest in nearby teachers. When they accept, contact details unlock here.'
                                    : 'Browse nearby teachers and express interest to see them here.'}
                            </p>
                            {tab === 'discovery' && (
                                <p className="text-sm text-gray-400 mt-3 italic flex items-center justify-center gap-1.5">
                                    <Lightbulb className="w-4 h-4" />
                                    Send interest to 3-5 teachers to increase your chances of matching
                                </p>
                            )}
                            {tab !== 'discovery' && (
                                <button
                                    onClick={() => setTab('discovery')}
                                    className="mt-6 inline-block px-6 py-3 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition-colors"
                                >
                                    Browse Teachers
                                </button>
                            )}
                        </div>
                    )}
                </div>
            </main>
        </div>
    );
}
