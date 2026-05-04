'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import api from '@/lib/api';
import {
    Bell,
    Check,
    X,
    Loader2,
    ArrowLeft,
    Clock,
    CheckCircle2,
    XCircle,
    ShieldCheck,
    Send,
    Phone,
    School,
    MapPin
} from 'lucide-react';
import Link from 'next/link';

export default function InterestsPage() {
    const router = useRouter();
    const [loading, setLoading] = useState(true);
    const [received, setReceived] = useState<any[]>([]);
    const [sent, setSent] = useState<any[]>([]);
    const [tab, setTab] = useState<'received' | 'sent'>('received');
    const [processing, setProcessing] = useState<number | null>(null);
    const [error, setError] = useState<string | null>(null);

    const fetchData = async () => {
        setLoading(true);
        try {
            const [receivedRes, sentRes] = await Promise.all([
                api.get('/interest/received'),
                api.get('/interest/sent')
            ]);
            setReceived(receivedRes.data.data || []);
            setSent(sentRes.data.data || []);
        } catch (err: any) {
            if (err.response?.status === 401) {
                router.push('/login');
            }
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, [router]);

    const handleAction = async (interestId: number, action: 'accept' | 'reject') => {
        setProcessing(interestId);
        setError(null);
        try {
            await api.post(`/interest/${interestId}/${action}`, {});
            await fetchData();
        } catch (err: any) {
            const msg = err?.response?.data?.message || err?.message || 'Something went wrong';
            setError(msg);
            setTimeout(() => setError(null), 5000);
        } finally {
            setProcessing(null);
        }
    };

    const handleWithdraw = async (interestId: number) => {
        setProcessing(interestId);
        setError(null);
        try {
            await api.delete(`/interest/${interestId}`);
            await fetchData();
        } catch (err: any) {
            const msg = err?.response?.data?.message || err?.message || 'Failed to withdraw';
            setError(msg);
            setTimeout(() => setError(null), 5000);
        } finally {
            setProcessing(null);
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
            <header className="bg-white border-b sticky top-0 z-10">
                <div className="max-w-4xl mx-auto px-4 py-4 flex items-center justify-between">
                    <div className="flex items-center gap-4">
                        <Link href="/dashboard" className="p-2 hover:bg-gray-100 rounded-full transition-colors">
                            <ArrowLeft className="w-6 h-6 text-gray-600" />
                        </Link>
                        <h1 className="text-2xl font-bold text-gray-900">Interests</h1>
                    </div>
                </div>
            </header>

            <main className="max-w-4xl mx-auto px-4 pt-8">
                <div className="flex p-1 bg-gray-200 rounded-2xl mb-8 w-fit mx-auto sm:mx-0">
                    <button
                        onClick={() => setTab('received')}
                        className={`px-8 py-3 rounded-xl font-bold text-sm transition-all flex items-center gap-1.5 ${tab === 'received' ? 'bg-white text-blue-600 shadow-sm' : 'text-gray-500 hover:text-gray-700'}`}
                    >
                        <Bell className="w-4 h-4" />
                        Received ({received.length})
                    </button>
                    <button
                        onClick={() => setTab('sent')}
                        className={`px-8 py-3 rounded-xl font-bold text-sm transition-all flex items-center gap-1.5 ${tab === 'sent' ? 'bg-white text-purple-600 shadow-sm' : 'text-gray-500 hover:text-gray-700'}`}
                    >
                        <Send className="w-4 h-4" />
                        Sent ({sent.length})
                    </button>
                </div>

                {error && (
                    <div className="mb-4 px-4 py-3 bg-red-50 border border-red-200 rounded-xl text-sm font-medium text-red-700 flex items-center gap-2">
                        <XCircle className="w-4 h-4 flex-shrink-0" />
                        {error}
                    </div>
                )}

                <div className="space-y-6">
                    {tab === 'received' && (
                        <>
                            {(() => {
                                const activeItems = received.filter(i => i.status === 'PENDING');
                                const rejectedItems = received.filter(i => i.status === 'REJECTED');
                                const withdrawnItems = received.filter(i => i.status === 'WITHDRAWN');
                                const mutualItems = received.filter(i => i.status === 'ACCEPTED');

                                return (
                                    <>
                                        {activeItems.map((item, idx) => (
                                            <div key={item.id}>
                                                {idx > 0 && <div className="border-t border-gray-100 -mx-4 mb-6" />}
                                                <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 relative overflow-hidden">
                                                    <div className="absolute top-0 left-0 w-1.5 h-full bg-yellow-400" />
                                                    <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                                                        <div className="flex items-center gap-4">
                                                            <div className="w-12 h-12 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center">
                                                                <MapPin className="w-6 h-6" />
                                                            </div>
                                                            <div>
                                                                <h3 className="text-lg font-bold text-gray-900">Under {item.fromTeacherBlockName || 'Unknown'} Block</h3>
                                                                <p className="text-sm text-gray-500">{item.distanceKm ? Math.round(item.distanceKm) + ' km away from your preferred location' : ''}</p>
                                                                <span className="text-xs text-gray-400 flex items-center gap-1 mt-1">
                                                                    <Clock className="w-3 h-3" />
                                                                    {new Date(item.createdAt).toLocaleDateString()}
                                                                </span>
                                                            </div>
                                                        </div>
                                                        <div className="flex items-center gap-3">
                                                            <button onClick={() => handleAction(item.id, 'reject')} disabled={processing !== null} className="p-3 text-red-500 hover:bg-red-50 rounded-xl transition-colors">
                                                                <X className="w-5 h-5" />
                                                            </button>
                                                            <button onClick={() => handleAction(item.id, 'accept')} disabled={processing !== null} className="px-6 py-3 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-all shadow-lg shadow-blue-100 flex items-center gap-2 font-bold">
                                                                {processing === item.id ? <Loader2 className="w-5 h-5 animate-spin" /> : <><Check className="w-5 h-5" /> Accept</>}
                                                            </button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        ))}
                                        {rejectedItems.map((item, idx) => (
                                            <div key={item.id}>
                                                {(idx > 0 || (idx === 0 && activeItems.length > 0)) && <div className="border-t border-gray-100 -mx-4 mb-6" />}
                                                <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 relative overflow-hidden">
                                                    <div className="absolute top-0 left-0 w-1.5 h-full bg-red-500" />
                                                    <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                                                        <div className="flex items-center gap-4">
                                                            <div className="w-12 h-12 rounded-xl bg-gray-50 text-gray-500 flex items-center justify-center">
                                                                <MapPin className="w-6 h-6" />
                                                            </div>
                                                            <div>
                                                                <h3 className="text-lg font-bold text-gray-700">Under {item.fromTeacherBlockName || 'Unknown'} Block</h3>
                                                                <p className="text-sm text-gray-500">{item.distanceKm ? Math.round(item.distanceKm) + ' km away from your preferred location' : ''}</p>
                                                                <span className="text-xs text-gray-400 flex items-center gap-1 mt-1">
                                                                    <Clock className="w-3 h-3" />
                                                                    {new Date(item.createdAt).toLocaleDateString()}
                                                                </span>
                                                            </div>
                                                        </div>
                                                        <span className="text-sm font-bold text-red-400 flex items-center gap-1"><XCircle className="w-4 h-4" /> Rejected</span>
                                                    </div>
                                                </div>
                                            </div>
                                        ))}
                                        {withdrawnItems.map((item, idx) => (
                                            <div key={item.id}>
                                                {(idx > 0 || activeItems.length > 0 || rejectedItems.length > 0) && <div className="border-t border-gray-100 -mx-4 mb-6" />}
                                                <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 relative overflow-hidden">
                                                    <div className="absolute top-0 left-0 w-1.5 h-full bg-gray-400" />
                                                    <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                                                        <div className="flex items-center gap-4">
                                                            <div className="w-12 h-12 rounded-xl bg-gray-50 text-gray-500 flex items-center justify-center">
                                                                <MapPin className="w-6 h-6" />
                                                            </div>
                                                            <div>
                                                                <h3 className="text-lg font-bold text-gray-700">Under {item.fromTeacherBlockName || 'Unknown'} Block</h3>
                                                                <p className="text-sm text-gray-500">{item.distanceKm ? Math.round(item.distanceKm) + ' km away from your preferred location' : ''}</p>
                                                                <span className="text-xs text-gray-400 flex items-center gap-1 mt-1">
                                                                    <Clock className="w-3 h-3" />
                                                                    {new Date(item.createdAt).toLocaleDateString()}
                                                                </span>
                                                            </div>
                                                        </div>
                                                        <span className="text-sm font-bold text-gray-400 flex items-center gap-1"><XCircle className="w-4 h-4" /> Withdrawn</span>
                                                    </div>
                                                </div>
                                            </div>
                                        ))}
                                        {mutualItems.map((item, idx) => (
                                            <div key={item.id}>
                                                {(idx > 0 || activeItems.length > 0 || rejectedItems.length > 0 || withdrawnItems.length > 0) && <div className="border-t border-gray-100 -mx-4 mb-6" />}
                                                <div className="bg-white rounded-2xl p-6 shadow-sm border border-green-200 relative overflow-hidden bg-gradient-to-br from-white to-green-50/30">
                                                    <div className="absolute top-0 left-0 w-1.5 h-full bg-green-500" />
                                                    <div className="flex items-center gap-1.5 mb-3">
                                                        <ShieldCheck className="w-4 h-4 text-green-600" />
                                                        <span className="text-xs font-bold text-green-600 uppercase tracking-wider">Mutual Match</span>
                                                    </div>
                                                    <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                                                        <div className="flex items-center gap-4">
                                                            <div className="w-12 h-12 rounded-xl bg-green-100 text-green-600 flex items-center justify-center font-bold text-lg">
                                                                {item.fromTeacherName ? item.fromTeacherName[0] : '?'}
                                                            </div>
                                                            <div>
                                                                <h3 className="text-lg font-bold text-gray-900">{item.fromTeacherName || 'Teacher'}</h3>
                                                                <p className="text-sm text-gray-500">Under {item.fromTeacherBlockName || 'Unknown'} Block &middot; {item.distanceKm ? Math.round(item.distanceKm) + ' km from your preferred location' : ''}</p>
                                                                <span className="text-xs text-gray-400 flex items-center gap-1 mt-1">
                                                                    <Clock className="w-3 h-3" />
                                                                    {new Date(item.createdAt).toLocaleDateString()}
                                                                </span>
                                                            </div>
                                                        </div>
                                                        <div className="space-y-1">
                                                            {item.fromTeacherSchool && <div className="flex items-center gap-1.5 text-sm text-green-700"><School className="w-4 h-4" />{item.fromTeacherSchool}</div>}
                                                            {item.fromTeacherPhone && <div className="flex items-center gap-1.5 text-sm font-semibold text-green-800"><Phone className="w-4 h-4" />{item.fromTeacherPhone}</div>}
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        ))}
                                        {received.length === 0 && (
                                            <div className="py-20 text-center bg-white rounded-2xl border border-dashed border-gray-200">
                                                <div className="w-20 h-20 bg-gray-50 rounded-full flex items-center justify-center mx-auto mb-6">
                                                    <Bell className="w-10 h-10 text-gray-300" />
                                                </div>
                                                <h3 className="text-xl font-bold text-gray-900">No interests received yet</h3>
                                                <p className="text-gray-500 mt-2 max-w-sm mx-auto font-medium">
                                                    When teachers discover you and express interest, their requests appear here. Accept to unlock their contact details.
                                                </p>
                                            </div>
                                        )}
                                    </>
                                );
                            })()}
                        </>
                    )}

                    {tab === 'sent' && (
                        <>
                            {(() => {
                                const activeItems = sent.filter(i => i.status === 'PENDING');
                                const rejectedItems = sent.filter(i => i.status === 'REJECTED');
                                const withdrawnItems = sent.filter(i => i.status === 'WITHDRAWN');
                                const mutualItems = sent.filter(i => i.status === 'ACCEPTED');

                                return (
                                    <>
                                        {activeItems.map((item, idx) => (
                                            <div key={item.id}>
                                                {idx > 0 && <div className="border-t border-gray-100 -mx-4 mb-6" />}
                                                <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 relative overflow-hidden">
                                                    <div className="absolute top-0 left-0 w-1.5 h-full bg-yellow-400" />
                                                    <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                                                        <div className="flex items-center gap-4">
                                                            <div className="w-12 h-12 rounded-xl bg-purple-50 text-purple-600 flex items-center justify-center">
                                                                <MapPin className="w-6 h-6" />
                                                            </div>
                                                            <div>
                                                                <h3 className="text-lg font-bold text-gray-900">Under {item.toTeacherBlockName || 'Unknown'} Block</h3>
                                                                <p className="text-sm text-gray-500">{item.distanceKm ? Math.round(item.distanceKm) + ' km away from your preferred location' : ''}</p>
                                                                <span className="text-xs text-gray-400 flex items-center gap-1 mt-1">
                                                                    <Clock className="w-3 h-3" />
                                                                    {new Date(item.createdAt).toLocaleDateString()}
                                                                </span>
                                                            </div>
                                                        </div>
                                                        <div className="flex items-center gap-3">
                                                            <span className="text-sm font-bold text-gray-400 italic">Waiting for response...</span>
                                                            <button onClick={() => handleWithdraw(item.id)} disabled={processing !== null} className="px-4 py-2 text-sm font-bold text-gray-500 bg-gray-100 hover:bg-gray-200 rounded-xl transition-colors flex items-center gap-1">
                                                                {processing === item.id ? <Loader2 className="w-4 h-4 animate-spin" /> : <><XCircle className="w-4 h-4" /> Withdraw</>}
                                                            </button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        ))}
                                        {rejectedItems.map((item, idx) => (
                                            <div key={item.id}>
                                                {(idx > 0 || activeItems.length > 0) && <div className="border-t border-gray-100 -mx-4 mb-6" />}
                                                <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 relative overflow-hidden">
                                                    <div className="absolute top-0 left-0 w-1.5 h-full bg-red-500" />
                                                    <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                                                        <div className="flex items-center gap-4">
                                                            <div className="w-12 h-12 rounded-xl bg-gray-50 text-gray-500 flex items-center justify-center">
                                                                <MapPin className="w-6 h-6" />
                                                            </div>
                                                            <div>
                                                                <h3 className="text-lg font-bold text-gray-700">Under {item.toTeacherBlockName || 'Unknown'} Block</h3>
                                                                <p className="text-sm text-gray-500">{item.distanceKm ? Math.round(item.distanceKm) + ' km away from your preferred location' : ''}</p>
                                                                <span className="text-xs text-gray-400 flex items-center gap-1 mt-1">
                                                                    <Clock className="w-3 h-3" />
                                                                    {new Date(item.createdAt).toLocaleDateString()}
                                                                </span>
                                                            </div>
                                                        </div>
                                                        <span className="text-sm font-bold text-red-400 flex items-center gap-1"><XCircle className="w-4 h-4" /> Rejected</span>
                                                    </div>
                                                </div>
                                            </div>
                                        ))}
                                        {withdrawnItems.map((item, idx) => (
                                            <div key={item.id}>
                                                {(idx > 0 || activeItems.length > 0 || rejectedItems.length > 0) && <div className="border-t border-gray-100 -mx-4 mb-6" />}
                                                <div className="bg-white rounded-2xl p-6 shadow-sm border border-gray-100 relative overflow-hidden">
                                                    <div className="absolute top-0 left-0 w-1.5 h-full bg-gray-400" />
                                                    <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                                                        <div className="flex items-center gap-4">
                                                            <div className="w-12 h-12 rounded-xl bg-gray-50 text-gray-500 flex items-center justify-center">
                                                                <MapPin className="w-6 h-6" />
                                                            </div>
                                                            <div>
                                                                <h3 className="text-lg font-bold text-gray-700">Under {item.toTeacherBlockName || 'Unknown'} Block</h3>
                                                                <p className="text-sm text-gray-500">{item.distanceKm ? Math.round(item.distanceKm) + ' km away from your preferred location' : ''}</p>
                                                                <span className="text-xs text-gray-400 flex items-center gap-1 mt-1">
                                                                    <Clock className="w-3 h-3" />
                                                                    {new Date(item.createdAt).toLocaleDateString()}
                                                                </span>
                                                            </div>
                                                        </div>
                                                        <span className="text-sm font-bold text-gray-400 flex items-center gap-1"><XCircle className="w-4 h-4" /> Withdrawn</span>
                                                    </div>
                                                </div>
                                            </div>
                                        ))}
                                        {mutualItems.map((item, idx) => (
                                            <div key={item.id}>
                                                {(idx > 0 || activeItems.length > 0 || rejectedItems.length > 0 || withdrawnItems.length > 0) && <div className="border-t border-gray-100 -mx-4 mb-6" />}
                                                <div className="bg-white rounded-2xl p-6 shadow-sm border border-green-200 relative overflow-hidden bg-gradient-to-br from-white to-green-50/30">
                                                    <div className="absolute top-0 left-0 w-1.5 h-full bg-green-500" />
                                                    <div className="flex items-center gap-1.5 mb-3">
                                                        <ShieldCheck className="w-4 h-4 text-green-600" />
                                                        <span className="text-xs font-bold text-green-600 uppercase tracking-wider">Mutual Match</span>
                                                    </div>
                                                    <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                                                        <div className="flex items-center gap-4">
                                                            <div className="w-12 h-12 rounded-xl bg-green-100 text-green-600 flex items-center justify-center font-bold text-lg">
                                                                {item.toTeacherName ? item.toTeacherName[0] : '?'}
                                                            </div>
                                                            <div>
                                                                <h3 className="text-lg font-bold text-gray-900">{item.toTeacherName || 'Teacher'}</h3>
                                                                <p className="text-sm text-gray-500">Under {item.toTeacherBlockName || 'Unknown'} Block &middot; {item.distanceKm ? Math.round(item.distanceKm) + ' km from your preferred location' : ''}</p>
                                                                <span className="text-xs text-gray-400 flex items-center gap-1 mt-1">
                                                                    <Clock className="w-3 h-3" />
                                                                    {new Date(item.createdAt).toLocaleDateString()}
                                                                </span>
                                                            </div>
                                                        </div>
                                                        <div className="space-y-1">
                                                            {item.toTeacherSchool && <div className="flex items-center gap-1.5 text-sm text-green-700"><School className="w-4 h-4" />{item.toTeacherSchool}</div>}
                                                            {item.toTeacherPhone && <div className="flex items-center gap-1.5 text-sm font-semibold text-green-800"><Phone className="w-4 h-4" />{item.toTeacherPhone}</div>}
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        ))}
                                        {sent.length === 0 && (
                                            <div className="py-20 text-center bg-white rounded-2xl border border-dashed border-gray-200">
                                                <div className="w-20 h-20 bg-gray-50 rounded-full flex items-center justify-center mx-auto mb-6">
                                                    <Send className="w-10 h-10 text-gray-300" />
                                                </div>
                                                <h3 className="text-xl font-bold text-gray-900">No interests sent yet</h3>
                                                <p className="text-gray-500 mt-2 max-w-sm mx-auto font-medium">
                                                    Browse teachers near your home and express interest to start connecting.
                                                </p>
                                                <Link href="/matches" className="mt-6 inline-block px-8 py-3 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition-colors">
                                                    Browse Teachers
                                                </Link>
                                            </div>
                                        )}
                                    </>
                                );
                            })()}
                        </>
                    )}
                </div>
            </main>
        </div>
    );
}
