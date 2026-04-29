'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import api from '@/lib/api';
import {
    Bell,
    ArrowRightLeft,
    Check,
    X,
    Loader2,
    ArrowLeft,
    Clock,
    User,
    CheckCircle2,
    XCircle,
    MessageSquare
} from 'lucide-react';
import Link from 'next/link';

export default function InterestsPage() {
    const router = useRouter();
    const [loading, setLoading] = useState(true);
    const [received, setReceived] = useState<any[]>([]);
    const [sent, setSent] = useState<any[]>([]);
    const [tab, setTab] = useState<'received' | 'sent'>('received');
    const [processing, setProcessing] = useState<number | null>(null);

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
        try {
            await api.post(`/interest/${interestId}/${action}`, {});
            await fetchData();
        } catch (err) {
            console.error(`Failed to ${action} interest`, err);
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
            {/* Header */}
            <header className="bg-white border-b sticky top-0 z-10">
                <div className="max-w-4xl mx-auto px-4 py-4 flex items-center justify-between">
                    <div className="flex items-center gap-4">
                        <Link href="/dashboard" className="p-2 hover:bg-gray-100 rounded-full transition-colors">
                            <ArrowLeft className="w-6 h-6 text-gray-600" />
                        </Link>
                        <h1 className="text-2xl font-bold text-gray-900">Transfer Interests</h1>
                    </div>
                </div>
            </header>

            <main className="max-w-4xl mx-auto px-4 pt-8">
                {/* Tabs */}
                <div className="flex p-1 bg-gray-200 rounded-2xl mb-8 w-fit mx-auto sm:mx-0">
                    <button
                        onClick={() => setTab('received')}
                        className={`px-8 py-3 rounded-xl font-black text-sm transition-all ${tab === 'received' ? 'bg-white text-blue-600 shadow-sm' : 'text-gray-500 hover:text-gray-700'}`}
                    >
                        Received ({received.length})
                    </button>
                    <button
                        onClick={() => setTab('sent')}
                        className={`px-8 py-3 rounded-xl font-black text-sm transition-all ${tab === 'sent' ? 'bg-white text-blue-600 shadow-sm' : 'text-gray-500 hover:text-gray-700'}`}
                    >
                        Sent ({sent.length})
                    </button>
                </div>

                {/* Content List */}
                <div className="space-y-4">
                    {(tab === 'received' ? received : sent).map((item) => (
                        <div key={item.id} className="bg-white rounded-3xl p-6 shadow-sm border border-gray-100 hover:shadow-md transition-shadow relative overflow-hidden group">
                            {/* Status Indicator */}
                            <div className={`absolute top-0 left-0 w-1.5 h-full ${item.status === 'ACCEPTED' ? 'bg-green-500' :
                                    item.status === 'REJECTED' ? 'bg-red-500' : 'bg-yellow-400'
                                }`} />

                            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-6">
                                <div className="flex items-center gap-4">
                                    <div className={`w-14 h-14 rounded-2xl flex items-center justify-center font-black text-xl ${tab === 'received' ? 'bg-blue-50 text-blue-600' : 'bg-purple-50 text-purple-600'
                                        }`}>
                                        {(tab === 'received' ? item.senderName : item.receiverName)?.[0]}
                                    </div>
                                    <div>
                                        <h3 className="text-lg font-black text-gray-900 leading-tight">
                                            {tab === 'received' ? item.senderName : item.receiverName}
                                        </h3>
                                        <div className="flex items-center gap-2 mt-1">
                                            <span className="text-xs font-bold text-gray-400 uppercase tracking-widest flex items-center gap-1">
                                                <Clock className="w-3 h-3" />
                                                {new Date(item.createdAt).toLocaleDateString()}
                                            </span>
                                            <span className={`text-[10px] font-black px-2 py-0.5 rounded-full ${item.status === 'ACCEPTED' ? 'bg-green-50 text-green-600' :
                                                    item.status === 'REJECTED' ? 'bg-red-50 text-red-600' : 'bg-yellow-50 text-yellow-600'
                                                }`}>
                                                {item.status}
                                            </span>
                                        </div>
                                    </div>
                                </div>

                                <div className="flex items-center gap-3">
                                    {tab === 'received' && item.status === 'PENDING' ? (
                                        <>
                                            <button
                                                onClick={() => handleAction(item.id, 'reject')}
                                                disabled={processing !== null}
                                                className="flex-1 sm:flex-none p-3 text-red-500 hover:bg-red-50 rounded-2xl transition-colors flex items-center justify-center gap-2 font-bold"
                                            >
                                                <X className="w-6 h-6" />
                                                <span className="sm:hidden">Reject</span>
                                            </button>
                                            <button
                                                onClick={() => handleAction(item.id, 'accept')}
                                                disabled={processing !== null}
                                                className="flex-1 sm:flex-none px-6 py-3 bg-blue-600 text-white rounded-2xl hover:bg-blue-700 transition-all shadow-lg shadow-blue-100 flex items-center justify-center gap-2 font-black"
                                            >
                                                {processing === item.id ? (
                                                    <Loader2 className="w-5 h-5 animate-spin" />
                                                ) : (
                                                    <>
                                                        <Check className="w-5 h-5" />
                                                        Accept Request
                                                    </>
                                                )}
                                            </button>
                                        </>
                                    ) : item.status === 'ACCEPTED' ? (
                                        <div className="flex gap-2">
                                            <button className="px-6 py-3 bg-green-50 text-green-600 rounded-2xl font-black flex items-center gap-2 cursor-default">
                                                <CheckCircle2 className="w-5 h-5" />
                                                Mutual Connection
                                            </button>
                                            <button className="p-3 bg-blue-50 text-blue-600 rounded-2xl hover:bg-blue-600 hover:text-white transition-all shadow-sm">
                                                <MessageSquare className="w-6 h-6" />
                                            </button>
                                        </div>
                                    ) : item.status === 'REJECTED' ? (
                                        <span className="text-sm font-bold text-red-400 flex items-center gap-1">
                                            <XCircle className="w-4 h-4" />
                                            Closed
                                        </span>
                                    ) : (
                                        <span className="text-sm font-bold text-gray-400 italic">Waiting for response...</span>
                                    )}
                                </div>
                            </div>
                        </div>
                    ))}

                    {(tab === 'received' ? received : sent).length === 0 && (
                        <div className="py-20 text-center bg-white rounded-3xl border border-dashed border-gray-200">
                            <div className="w-20 h-20 bg-gray-50 rounded-full flex items-center justify-center mx-auto mb-6">
                                <Bell className="w-10 h-10 text-gray-300" />
                            </div>
                            <h3 className="text-xl font-bold text-gray-900">No {tab} interests yet</h3>
                            <p className="text-gray-500 mt-2 max-w-xs mx-auto font-medium">
                                {tab === 'received'
                                    ? "When other teachers find you and express interest, they will appear here."
                                    : "Browse for matches and express interest to see them here."}
                            </p>
                            {tab === 'sent' && (
                                <Link href="/matches" className="mt-6 inline-block px-8 py-3 bg-blue-600 text-white rounded-xl font-bold hover:bg-blue-700 transition-colors">
                                    Find Matches
                                </Link>
                            )}
                        </div>
                    )}
                </div>
            </main>
        </div>
    );
}
