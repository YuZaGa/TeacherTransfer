'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import api from '@/lib/api';
import {
    Star,
    Check,
    Zap,
    Map as MapIcon,
    Search,
    Bell,
    ArrowRightLeft,
    Loader2,
    ArrowLeft,
    Crown
} from 'lucide-react';
import Link from 'next/link';

export default function SubscriptionPage() {
    const router = useRouter();
    const [loading, setLoading] = useState(true);
    const [teacher, setTeacher] = useState<any>(null);
    const [plans, setPlans] = useState<any[]>([]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [meRes, planRes] = await Promise.all([
                    api.get('/teacher/me'),
                    api.get('/payment/plans')
                ]);
                setTeacher(meRes.data.data);
                setPlans(planRes.data.data || []);
            } catch (err: any) {
                if (err.response?.status === 401) {
                    router.push('/login');
                }
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [router]);

    const handleSubscribe = async (planId: number) => {
        try {
            const response = await api.post('/payment/create-order', { planId });
            const order = response.data.data;

            // Integration with Razorpay would happen here
            alert(`Order ${order.orderId} created for ₹${order.amount / 100}. Please proceed with payment.`);

        } catch (err) {
            console.error('Failed to create order', err);
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
                        <h1 className="text-2xl font-bold text-gray-900">Subscription Plans</h1>
                    </div>
                </div>
            </header>

            <main className="max-w-5xl mx-auto px-4 pt-12">
                <div className="text-center mb-16">
                    <h2 className="text-4xl font-black text-gray-900 mb-4 tracking-tight">Upgrade your search potential</h2>
                    <p className="text-xl text-gray-500 max-w-2xl mx-auto">Get exclusive access to interactive map exploration.</p>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-8 max-w-4xl mx-auto">
                    {/* Free Plan (Default) */}
                    <div className="bg-white rounded-3xl p-8 border border-gray-100 shadow-sm relative overflow-hidden flex flex-col">
                        <div className="mb-8">
                            <h3 className="text-xl font-bold text-gray-900 mb-2">Free Plan</h3>
                            <p className="text-gray-500 text-sm">Basic matching for every teacher.</p>
                        </div>
                        <div className="mb-8">
                            <span className="text-4xl font-black text-gray-900">₹0</span>
                            <span className="text-gray-400">/ forever</span>
                        </div>
                        <ul className="space-y-4 mb-12 flex-1">
                            {['Direct Matching', 'Unlimited Interest Sending', 'Profile Customization'].map((feature) => (
                                <li key={feature} className="flex items-center gap-3 text-gray-600 font-medium">
                                    <div className="bg-gray-50 p-1 rounded-full"><Check className="w-4 h-4 text-gray-400" /></div>
                                    {feature}
                                </li>
                            ))}
                        </ul>
                        <button disabled className="w-full py-4 bg-gray-100 text-gray-400 font-black rounded-2xl cursor-default">
                            Current Plan
                        </button>
                    </div>

                    {/* Premium Plan (Hardcoded style for now) */}
                    {plans.map((plan) => (
                        <div key={plan.id} className="bg-white rounded-3xl p-8 border-4 border-blue-600 shadow-2xl relative overflow-hidden flex flex-col scale-105 transform">
                            <div className="absolute top-0 right-0 pt-4 pr-4">
                                <Crown className="text-blue-600 w-8 h-8 opacity-20" />
                            </div>
                            <div className="mb-8">
                                <div className="bg-blue-600 text-white text-[10px] font-black uppercase tracking-widest px-3 py-1 rounded-full w-fit mb-3">
                                    Most Popular
                                </div>
                                <h3 className="text-xl font-bold text-gray-900 mb-2">{plan.name}</h3>
                                <p className="text-gray-500 text-sm">{plan.description}</p>
                            </div>
                            <div className="mb-8">
                                <span className="text-4xl font-black text-gray-900">₹{plan.price}</span>
                                <span className="text-gray-400">/ {plan.durationDays} days</span>
                            </div>
                            <ul className="space-y-4 mb-12 flex-1">
                                {[
                                    'Interactive Map Discovery',
                                    'Priority in search results',
                                    'Extended Search Radius (100km)',
                                    'Email & SMS Alerts'
                                ].map((feature) => (
                                    <li key={feature} className="flex items-center gap-3 text-gray-900 font-bold">
                                        <div className="bg-blue-50 p-1 rounded-full"><Check className="w-4 h-4 text-blue-600" /></div>
                                        {feature}
                                    </li>
                                ))}
                                <li className="flex items-center gap-3 text-gray-900 font-bold opacity-30">
                                    <div className="bg-blue-50 p-1 rounded-full"><Zap className="w-4 h-4 text-blue-600" /></div>
                                    Coming Soon: Video Consultation
                                </li>
                            </ul>
                            <button
                                onClick={() => handleSubscribe(plan.id)}
                                className="w-full py-4 bg-blue-600 text-white font-black rounded-2xl hover:bg-blue-700 transition-all shadow-xl shadow-blue-200 active:scale-95 flex items-center justify-center gap-2"
                            >
                                <Zap className="w-5 h-5 fill-white" />
                                Get Premium Now
                            </button>
                        </div>
                    ))}
                </div>

                {/* FAQ or reassurance */}
                <div className="mt-20 text-center">
                    <p className="text-gray-400 font-medium">Secure payments powered by Razorpay. Cancel anytime.</p>
                </div>
            </main>
        </div>
    );
}
