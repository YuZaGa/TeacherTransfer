'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useSession } from 'next-auth/react';
import api from '@/lib/api';
import {
    Users,
    Bell,
    CreditCard,
    Search,
    Map as MapIcon,
    Loader2,
    CheckCircle2,
    Clock,
    XCircle,
    Star,
    Heart,
    Eye,
    Send,
    ShieldCheck,
    MapPin
} from 'lucide-react';
import Link from 'next/link';

export default function DashboardPage() {
    const router = useRouter();
    const { data: session } = useSession();
    const [loading, setLoading] = useState(true);
    const [teacher, setTeacher] = useState<any>(null);
    const [discoveryMatches, setDiscoveryMatches] = useState<any[]>([]);
    const [mutualMatches, setMutualMatches] = useState<any[]>([]);
    const [receivedInterests, setReceivedInterests] = useState<any[]>([]);
    const [sentInterests, setSentInterests] = useState<any[]>([]);
    const [sendingInterest, setSendingInterest] = useState<number | null>(null);
    const [sentInterestIds, setSentInterestIds] = useState<Set<number>>(new Set());

    const fetchDashboardData = async () => {
        try {
            const [meRes, discRes, mutRes, rcvRes, sntRes] = await Promise.all([
                api.get('/teacher/me'),
                api.get('/matches'),
                api.get('/matches/mutual'),
                api.get('/interest/received'),
                api.get('/interest/sent')
            ]);

            setTeacher(meRes.data.data);
            setDiscoveryMatches(discRes.data.data || []);
            setMutualMatches(mutRes.data.data || []);
            setReceivedInterests(rcvRes.data.data || []);
            setSentInterests(sntRes.data.data || []);

            const sentIds = new Set<number>(
                (sntRes.data.data || [])
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

    useEffect(() => {
        fetchDashboardData();
    }, [router]);

    const handleSendInterest = async (teacherId: number) => {
        setSendingInterest(teacherId);
        try {
            await api.post(`/interest/${teacherId}`);
            await fetchDashboardData();
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

    const displayName = teacher?.name || session?.user?.name || 'Teacher';
    const userImage = session?.user?.image;
    const isPremium = teacher?.subscriptionStatus > 0;
    const preferredArea = teacher?.preferredLocation?.blockName
        || teacher?.preferredLocation?.districtName
        || 'your area';

    return (
        <div className="min-h-screen bg-gray-50 pb-12">
            <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-8">
                <div className="mb-8">
                    <h1 className="text-3xl font-bold text-gray-900">Welcome back, {displayName.split(' ')[0]}!</h1>
                    <p className="text-gray-500 mt-1">Discover transfer opportunities near your home location.</p>
                </div>

                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
                    <div className="bg-white p-5 rounded-2xl shadow-sm border border-gray-100 text-center">
                        <div className="bg-blue-50 p-2.5 rounded-xl text-blue-600 inline-block mb-2">
                            <Search className="w-6 h-6" />
                        </div>
                        <p className="text-2xl font-bold text-gray-900">{discoveryMatches.length}</p>
                        <p className="text-xs font-medium text-gray-500 uppercase tracking-wider">Explore Nearby</p>
                    </div>
                    <div className="bg-white p-5 rounded-2xl shadow-sm border border-gray-100 text-center">
                        <div className="bg-green-50 p-2.5 rounded-xl text-green-600 inline-block mb-2">
                            <ShieldCheck className="w-6 h-6" />
                        </div>
                        <p className="text-2xl font-bold text-gray-900">{mutualMatches.length}</p>
                        <p className="text-xs font-medium text-gray-500 uppercase tracking-wider">Mutual</p>
                    </div>
                    <div className="bg-white p-5 rounded-2xl shadow-sm border border-gray-100 text-center">
                        <div className="bg-yellow-50 p-2.5 rounded-xl text-yellow-600 inline-block mb-2">
                            <Bell className="w-6 h-6" />
                        </div>
                        <p className="text-2xl font-bold text-gray-900">{receivedInterests.length}</p>
                        <p className="text-xs font-medium text-gray-500 uppercase tracking-wider">Received</p>
                    </div>
                    <div className="bg-white p-5 rounded-2xl shadow-sm border border-gray-100 text-center">
                        <div className="bg-purple-50 p-2.5 rounded-xl text-purple-600 inline-block mb-2">
                            <Send className="w-6 h-6" />
                        </div>
                        <p className="text-2xl font-bold text-gray-900">{sentInterests.length}</p>
                        <p className="text-xs font-medium text-gray-500 uppercase tracking-wider">Sent</p>
                    </div>
                </div>

                <div className="space-y-8">
                    {/* Section 1: Matches Near Your Home (PRIMARY) */}
                    <section>
                        <div className="flex items-center justify-between mb-4">
                            <h2 className="text-xl font-bold text-gray-900 flex items-center gap-2">
                                <MapPin className="text-blue-600 w-5 h-5" />
                                Teachers Near Your Home
                            </h2>
                            <Link href="/matches" className="text-sm font-bold text-blue-600 hover:text-blue-700">
                                Browse All
                            </Link>
                        </div>
                        {discoveryMatches.length > 0 ? (
                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                                {discoveryMatches.slice(0, 6).map((match: any) => (
                                    <MatchCard
                                        key={match.teacher?.id || match.id}
                                        match={match}
                                        sendingInterest={sendingInterest}
                                        sentInterestIds={sentInterestIds}
                                        onSendInterest={handleSendInterest}
                                    />
                                ))}
                            </div>
                        ) : (
                            <EmptyState
                                message="Browse teachers currently posted near your preferred location."
                                actionLabel={`Browse Teachers Near ${preferredArea}`}
                                actionHref="/matches"
                                tip="Send interest to 3-5 teachers to increase your chances of matching."
                            />
                        )}
                    </section>

                    {/* Section 2: Mutual Matches */}
                    <section>
                        <div className="flex items-center justify-between mb-4">
                            <h2 className="text-xl font-bold text-gray-900 flex items-center gap-2">
                                <ShieldCheck className="text-green-600 w-5 h-5" />
                                Mutual Matches
                            </h2>
                            <Link href="/matches" className="text-sm font-bold text-blue-600 hover:text-blue-700">
                                View All
                            </Link>
                        </div>
                        {mutualMatches.length > 0 ? (
                            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                                {mutualMatches.slice(0, 6).map((match: any) => (
                                    <MutualMatchCard key={match.teacher?.id || match.id} match={match} />
                                ))}
                            </div>
                        ) : (
                            <div className="bg-white rounded-2xl p-8 text-center border border-gray-100">
                                <p className="text-gray-600 mb-2">No mutual matches yet.</p>
                                <p className="text-gray-400 text-sm">Express interest in teachers near your home. When they accept, contact details unlock here.</p>
                            </div>
                        )}
                    </section>

                    {/* Section 3: Interests Received */}
                    <section>
                        <div className="flex items-center justify-between mb-4">
                            <h2 className="text-xl font-bold text-gray-900 flex items-center gap-2">
                                <Bell className="text-yellow-600 w-5 h-5" />
                                Interests Received
                            </h2>
                            <Link href="/interests" className="text-sm font-bold text-blue-600 hover:text-blue-700">
                                View All
                            </Link>
                        </div>
                        {receivedInterests.length > 0 ? (
                            <div className="space-y-3">
                                {receivedInterests.slice(0, 3).map((item: any) => (
                                    <div key={item.id} className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100 flex items-center justify-between">
                                        <div className="flex items-center gap-3">
                                            <div className="w-10 h-10 bg-yellow-50 text-yellow-600 rounded-xl flex items-center justify-center">
                                                <Bell className="w-5 h-5" />
                                            </div>
                                            <div>
                                                <p className="font-bold text-gray-900">Under {item.fromTeacherBlockName || 'Unknown'} Block</p>
                                                <p className="text-sm text-gray-500">{item.distanceKm ? Math.round(item.distanceKm) + ' km from your preferred location' : ''} &middot; {new Date(item.createdAt).toLocaleDateString()}</p>
                                            </div>
                                        </div>
                                        <span className={`text-xs font-bold px-3 py-1 rounded-full ${
                                            item.status === 'ACCEPTED' ? 'bg-green-50 text-green-600' :
                                            item.status === 'REJECTED' ? 'bg-red-50 text-red-600' :
                                            item.status === 'WITHDRAWN' ? 'bg-gray-50 text-gray-500' :
                                            'bg-yellow-50 text-yellow-600'
                                        }`}>
                                            {item.status === 'ACCEPTED' ? 'Accepted' :
                                             item.status === 'REJECTED' ? 'Rejected' :
                                             item.status === 'WITHDRAWN' ? 'Withdrawn' :
                                             'Pending'}
                                        </span>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <div className="bg-white rounded-2xl p-8 text-center border border-gray-100">
                                <p className="text-gray-600 mb-2">No interests received yet.</p>
                                <p className="text-gray-400 text-sm">When teachers discover you and express interest, their requests appear here.</p>
                            </div>
                        )}
                    </section>

                    {/* Section 4: Interests Sent */}
                    <section>
                        <div className="flex items-center justify-between mb-4">
                            <h2 className="text-xl font-bold text-gray-900 flex items-center gap-2">
                                <Send className="text-purple-600 w-5 h-5" />
                                Interests Sent
                            </h2>
                            <Link href="/interests" className="text-sm font-bold text-blue-600 hover:text-blue-700">
                                View All
                            </Link>
                        </div>
                        {sentInterests.length > 0 ? (
                            <div className="space-y-3">
                                {sentInterests.slice(0, 3).map((item: any) => (
                                    <div key={item.id} className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100 flex items-center justify-between">
                                        <div className="flex items-center gap-3">
                                            <div className="w-10 h-10 bg-purple-50 text-purple-600 rounded-xl flex items-center justify-center">
                                                <Send className="w-5 h-5" />
                                            </div>
                                            <div>
                                                <p className="font-bold text-gray-900">Under {item.toTeacherBlockName || 'Unknown'} Block</p>
                                                <p className="text-sm text-gray-500">{item.distanceKm ? Math.round(item.distanceKm) + ' km from your preferred location' : ''} &middot; {new Date(item.createdAt).toLocaleDateString()}</p>
                                            </div>
                                        </div>
                                        <span className={`text-xs font-bold px-3 py-1 rounded-full ${
                                            item.status === 'ACCEPTED' ? 'bg-green-50 text-green-600' :
                                            item.status === 'REJECTED' ? 'bg-red-50 text-red-600' :
                                            item.status === 'WITHDRAWN' ? 'bg-gray-50 text-gray-500' :
                                            'bg-yellow-50 text-yellow-600'
                                        }`}>
                                            {item.status === 'ACCEPTED' ? 'Accepted' :
                                             item.status === 'REJECTED' ? 'Rejected' :
                                             item.status === 'WITHDRAWN' ? 'Withdrawn' :
                                             'Waiting'}
                                        </span>
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <div className="bg-white rounded-2xl p-8 text-center border border-gray-100">
                                <p className="text-gray-600 mb-2">No interests sent yet.</p>
                                <p className="text-gray-400 text-sm">Browse teachers near your home and express interest to start connecting.</p>
                            </div>
                        )}
                    </section>
                </div>
            </main>
        </div>
    );
}

function MatchCard({ match, sendingInterest, sentInterestIds, onSendInterest }: {
    match: any;
    sendingInterest: number | null;
    sentInterestIds: Set<number>;
    onSendInterest: (id: number) => void;
}) {
    const t = match.teacher || {};
    const teacherId = t.id;
    const isSent = sentInterestIds.has(teacherId);
    const distanceKm = t.distanceKm || match.distanceKm;

    return (
        <div className="bg-white rounded-2xl p-5 shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
            <div className="flex items-start justify-between mb-3">
                <div>
                    <p className="text-sm font-bold text-gray-900">
                        {Math.round(distanceKm)} km away
                    </p>
                    <p className="text-xs text-gray-500">{t.approxArea || 'Nearby'}</p>
                </div>
                <span className="text-xs font-bold px-2 py-1 bg-blue-50 text-blue-600 rounded-full">
                    {t.schoolType}
                </span>
            </div>
            <p className="text-sm font-semibold text-gray-700 mb-4">
                {t.subject}
            </p>
            <div className="flex gap-2">
                <button
                    onClick={() => onSendInterest(teacherId)}
                    disabled={isSent || sendingInterest === teacherId}
                    className={`flex-1 py-2.5 px-4 rounded-xl font-bold text-sm flex items-center justify-center gap-1.5 transition-all ${
                        isSent
                            ? 'bg-green-50 text-green-600 cursor-default'
                            : 'bg-blue-600 text-white hover:bg-blue-700 active:scale-95'
                    }`}
                >
                    {sendingInterest === teacherId ? (
                        <Loader2 className="w-4 h-4 animate-spin" />
                    ) : isSent ? (
                        <>
                            <CheckCircle2 className="w-4 h-4" />
                            Sent
                        </>
                    ) : (
                        <>
                            <Heart className="w-4 h-4" />
                            Interested
                        </>
                    )}
                </button>
            </div>
        </div>
    );
}

function MutualMatchCard({ match }: { match: any }) {
    const t = match.teacher || {};

    return (
        <div className="bg-white rounded-2xl p-5 shadow-sm border border-green-200 bg-gradient-to-br from-white to-green-50/30">
            <div className="flex items-center gap-3 mb-3">
                <div className="w-10 h-10 bg-green-100 text-green-600 rounded-full flex items-center justify-center font-bold text-lg">
                    {t.name ? t.name[0] : '?'}
                </div>
                <div>
                    <p className="font-bold text-gray-900">{t.name || 'Teacher'}</p>
                    <p className="text-xs text-gray-500">{t.schoolName || 'School details unlocked'}</p>
                </div>
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
                <div className="flex justify-between text-sm">
                    <span className="text-gray-500">Distance</span>
                    <span className="font-semibold text-gray-700">{Math.round(t.distanceKm || match.distanceKm)} km</span>
                </div>
            </div>
            {t.phone && (
                <div className="bg-green-50 p-3 rounded-xl text-center">
                    <p className="text-xs text-green-700 font-medium">Contact</p>
                    <p className="text-sm font-bold text-green-800">{t.phone}</p>
                </div>
            )}
        </div>
    );
}

function EmptyState({ message, actionLabel, actionHref, tip }: { message: string; actionLabel: string; actionHref: string; tip?: string }) {
    return (
        <div className="bg-white rounded-2xl p-12 text-center border border-dashed border-gray-200">
            <div className="inline-block p-4 bg-gray-50 rounded-full mb-4">
                <Search className="w-8 h-8 text-gray-400" />
            </div>
            <p className="text-gray-600 mb-4">{message}</p>
            <Link href={actionHref} className="inline-block px-6 py-3 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition-colors">
                {actionLabel}
            </Link>
            {tip && (
                <p className="text-sm text-gray-400 mt-4 italic">{tip}</p>
            )}
        </div>
    );
}
