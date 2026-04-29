'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import api from '@/lib/api';
import { Bell, Handshake, CreditCard, ArrowLeft, CheckCircle2, ChevronRight, Loader2 } from 'lucide-react';
import Link from 'next/link';

export default function NotificationsPage() {
    const router = useRouter();
    const [loading, setLoading] = useState(true);
    const [notifications, setNotifications] = useState<any[]>([]);

    useEffect(() => {
        const fetchNotifications = async () => {
            try {
                const response = await api.get('/notifications');
                setNotifications(response.data.data || []);
            } catch (err: any) {
                if (err.response?.status === 401) {
                    router.push('/login');
                } else {
                    // Fallback to dummy data for UI testing if endpoint doesn't exist
                    setNotifications([
                        { id: 1, type: 'MATCH', title: 'New Mutual Match!', message: 'You have a new mutual match with Sarah at Central High.', isRead: false, createdAt: new Date().toISOString() },
                        { id: 2, type: 'PING', title: 'Interest Accepted', message: 'John Doe accepted your transfer interest.', isRead: true, createdAt: new Date(Date.now() - 86400000).toISOString() },
                        { id: 3, type: 'SYSTEM', title: 'Subscription Updated', message: 'Your Premium plan is now active.', isRead: true, createdAt: new Date(Date.now() - 172800000).toISOString() },
                    ]);
                }
            } finally {
                setLoading(false);
            }
        };

        fetchNotifications();
    }, [router]);

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <Loader2 className="w-12 h-12 text-blue-600 animate-spin" />
            </div>
        );
    }

    const getIcon = (type: string) => {
        switch (type) {
            case 'MATCH': return <Handshake className="w-6 h-6 text-green-600" />;
            case 'PING': return <Bell className="w-6 h-6 text-blue-600" />;
            case 'SYSTEM': return <CreditCard className="w-6 h-6 text-purple-600" />;
            default: return <Bell className="w-6 h-6 text-gray-600" />;
        }
    };

    const getBgColor = (type: string) => {
        switch (type) {
            case 'MATCH': return 'bg-green-100';
            case 'PING': return 'bg-blue-100';
            case 'SYSTEM': return 'bg-purple-100';
            default: return 'bg-gray-100';
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 pb-20">
            {/* Header */}
            <header className="bg-white border-b sticky top-0 z-10">
                <div className="max-w-4xl mx-auto px-4 py-4 flex items-center justify-between">
                    <div className="flex items-center gap-4">
                        <Link href="/dashboard" className="p-2 hover:bg-gray-100 rounded-full transition-colors">
                            <ArrowLeft className="w-6 h-6 text-gray-600" />
                        </Link>
                        <h1 className="text-2xl font-bold text-gray-900">Notifications</h1>
                    </div>
                    <button className="text-blue-600 font-bold text-sm hover:underline">
                        Mark all as read
                    </button>
                </div>
            </header>

            <main className="max-w-4xl mx-auto px-4 pt-8">
                <div className="space-y-4">
                    {notifications.map((notification) => (
                        <div key={notification.id} className={`bg-white rounded-3xl p-6 border ${notification.isRead ? 'border-gray-100' : 'border-blue-200 shadow-md'} transition-all relative overflow-hidden group hover:shadow-lg flex items-center justify-between`}>
                            {!notification.isRead && (
                                <div className="absolute top-1/2 -translate-y-1/2 left-4 w-2 h-2 rounded-full bg-blue-600"></div>
                            )}
                            <div className={`flex items-start gap-4 ${!notification.isRead ? 'pl-4' : ''}`}>
                                <div className={`w-14 h-14 rounded-2xl flex items-center justify-center shrink-0 ${getBgColor(notification.type)}`}>
                                    {getIcon(notification.type)}
                                </div>
                                <div>
                                    <h3 className="text-lg font-bold text-gray-900 mb-1">{notification.title}</h3>
                                    <p className="text-gray-600 font-medium mb-2">{notification.message}</p>
                                    <span className="text-xs font-bold text-gray-400 uppercase tracking-wider block">
                                        {new Date(notification.createdAt).toLocaleDateString()}
                                    </span>
                                </div>
                            </div>
                            <div className="p-3 bg-gray-50 rounded-2xl group-hover:bg-blue-50 transition-colors cursor-pointer shrink-0">
                                <ChevronRight className="w-6 h-6 text-gray-400 group-hover:text-blue-600 transition-colors" />
                            </div>
                        </div>
                    ))}

                    {notifications.length === 0 && (
                        <div className="py-20 text-center bg-white rounded-3xl border border-dashed border-gray-200">
                            <div className="w-20 h-20 bg-gray-50 rounded-full flex items-center justify-center mx-auto mb-6">
                                <CheckCircle2 className="w-10 h-10 text-gray-300" />
                            </div>
                            <h3 className="text-xl font-bold text-gray-900">All caught up!</h3>
                            <p className="text-gray-500 mt-2 font-medium">You don't have any new notifications.</p>
                        </div>
                    )}
                </div>
            </main>
        </div>
    );
}
