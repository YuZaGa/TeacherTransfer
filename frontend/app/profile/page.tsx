'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { useSession, signOut } from 'next-auth/react';
import api from '@/lib/api';
import { User, School, MapPin, Save, AlertCircle, Loader2, LogOut, CheckCircle, CreditCard } from 'lucide-react';

interface District {
    id: number;
    name: string;
}

interface Block {
    id: number;
    name: string;
}

export default function ProfilePage() {
    const router = useRouter();
    const { data: session, status: sessionStatus } = useSession();
    const [loading, setLoading] = useState(true);
    const [updating, setUpdating] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [teacher, setTeacher] = useState<any>(null);
    const [districts, setDistricts] = useState<District[]>([]);
    const [currentBlocks, setCurrentBlocks] = useState<Block[]>([]);
    const [preferredBlocks, setPreferredBlocks] = useState<Block[]>([]);

    useEffect(() => {
        if (sessionStatus === 'unauthenticated') {
            router.push('/login');
            return;
        }
        if (sessionStatus !== 'authenticated') return;

        const fetchProfile = async () => {
            try {
                const response = await api.get('/teacher/me');
                const data = response.data.data;
                setTeacher(data);

                const distRes = await api.get('/geography/districts');
                setDistricts(distRes.data.data);

                if (data.currentLocation?.districtId) {
                    const blocksRes = await api.get(`/geography/districts/${data.currentLocation.districtId}/blocks`);
                    setCurrentBlocks(blocksRes.data.data);
                }
                if (data.preferredLocation?.districtId) {
                    const blocksRes = await api.get(`/geography/districts/${data.preferredLocation.districtId}/blocks`);
                    setPreferredBlocks(blocksRes.data.data);
                }
            } catch (err: any) {
                setError('Failed to load profile. Please try again.');
                if (err.response?.status === 401) {
                    router.push('/login');
                }
            } finally {
                setLoading(false);
            }
        };

        fetchProfile();
    }, [router, sessionStatus]);

    const handleDistrictChange = async (districtId: string, type: 'current' | 'preferred') => {
        if (!districtId) return;

        try {
            const res = await api.get(`/geography/districts/${districtId}/blocks`);
            if (type === 'current') {
                setCurrentBlocks(res.data.data);
                setTeacher({
                    ...teacher,
                    currentLocation: {
                        ...(teacher.currentLocation || {}),
                        districtId: parseInt(districtId),
                        blockId: null
                    }
                });
            } else {
                setPreferredBlocks(res.data.data);
                setTeacher({
                    ...teacher,
                    preferredLocation: {
                        ...(teacher.preferredLocation || {}),
                        districtId: parseInt(districtId),
                        blockId: null
                    }
                });
            }
        } catch (err) {
            setError('Failed to load blocks.');
        }
    };

    const handleUpdate = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setSuccess('');
        setUpdating(true);

        try {
            await api.put('/teacher/me', teacher);
            setSuccess('Profile updated successfully!');
        } catch (err: any) {
            setError(err.response?.data?.message || 'Update failed.');
        } finally {
            setUpdating(false);
        }
    };

    const handleLogout = () => {
        signOut({ callbackUrl: '/login' });
    };

    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-[calc(100vh-4rem)]">
                <Loader2 className="w-12 h-12 text-blue-600 animate-spin" />
            </div>
        );
    }

    if (!teacher) {
        return (
            <div className="max-w-md mx-auto mt-20 p-6 bg-white rounded-2xl shadow-lg border border-gray-100 text-center">
                <AlertCircle className="w-12 h-12 text-red-400 mx-auto mb-4" />
                <h2 className="text-xl font-bold text-gray-900 mb-2">Could not load profile</h2>
                <p className="text-gray-500 mb-6">{error || 'An unexpected error occurred.'}</p>
                <button
                    onClick={() => window.location.reload()}
                    className="px-6 py-3 bg-blue-600 text-white font-bold rounded-xl hover:bg-blue-700 transition-colors"
                >
                    Retry
                </button>
            </div>
        );
    }

    return (
        <div className="max-w-5xl mx-auto p-6 md:p-8">
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                {/* Left Column: Summary Card */}
                <div className="lg:col-span-1 space-y-6">
                    <div className="bg-white rounded-2xl shadow-lg overflow-hidden border border-gray-100">
                        <div className="bg-gradient-to-br from-blue-600 to-indigo-700 p-8 text-center text-white">
                            <div className="w-24 h-24 rounded-full flex items-center justify-center mx-auto mb-4 border-4 border-white/30 overflow-hidden">
                                {session?.user?.image ? (
                                    <img src={session.user.image} alt="" className="w-full h-full object-cover" />
                                ) : (
                                    <div className="w-full h-full bg-white/20 backdrop-blur-sm flex items-center justify-center">
                                        <User size={48} />
                                    </div>
                                )}
                            </div>
                            <h1 className="text-2xl font-bold">{teacher.name}</h1>
                            <p className="text-blue-100 opacity-90 mt-1">{teacher.phone}</p>
                        </div>
                        <div className="p-6 space-y-4">
                            <div className="flex items-center justify-between text-sm py-2 border-b">
                                <span className="text-gray-500">Status</span>
                                <span className="font-bold text-green-600 bg-green-50 px-2 py-1 rounded">Active</span>
                            </div>
                            <div className="flex items-center justify-between text-sm py-2 border-b">
                                <span className="text-gray-500">Subscription</span>
                                <span className={teacher.subscriptionStatus > 0 ? 'text-blue-600 font-bold' : 'text-gray-400 font-medium'}>
                                    {teacher.subscriptionStatus > 0 ? 'Premium Plan' : 'Free User'}
                                </span>
                            </div>
                            <div className="flex items-center justify-between text-sm py-2">
                                <span className="text-gray-500">Member Since</span>
                                <span className="font-medium text-gray-700">
                                    {new Date(teacher.createdAt).toLocaleDateString()}
                                </span>
                            </div>
                        </div>
                        <div className="p-6 bg-gray-50">
                            <button
                                onClick={handleLogout}
                                className="w-full py-2 px-4 flex items-center justify-center gap-2 text-red-600 bg-white border border-red-100 rounded-lg font-bold hover:bg-red-50 transition-colors shadow-sm"
                            >
                                <LogOut size={18} />
                                Sign Out
                            </button>
                        </div>
                    </div>

                    <div className="bg-blue-50 rounded-2xl p-6 border border-blue-100">
                        <h3 className="text-blue-900 font-bold flex items-center mb-3">
                            <CreditCard className="w-5 h-5 mr-2" />
                            Quick Links
                        </h3>
                        <div className="space-y-3">
                            <button className="w-full text-left py-2 text-blue-700 hover:text-blue-900 font-medium text-sm transition-colors">
                                View Subscription Plans
                            </button>
                            <button className="w-full text-left py-2 text-blue-700 hover:text-blue-900 font-medium text-sm transition-colors">
                                Transaction History
                            </button>
                            <button className="w-full text-left py-2 text-blue-700 hover:text-blue-900 font-medium text-sm transition-colors">
                                Contact Support
                            </button>
                        </div>
                    </div>
                </div>

                {/* Right Column: Profile Content */}
                <div className="lg:col-span-2 space-y-8">
                    {error && (
                        <div className="p-4 bg-red-50 border-l-4 border-red-500 flex items-start text-red-700 rounded shadow-sm">
                            <AlertCircle className="w-5 h-5 mr-3 flex-shrink-0" />
                            <p className="text-sm font-medium">{error}</p>
                        </div>
                    )}

                    {success && (
                        <div className="p-4 bg-green-50 border-l-4 border-green-500 flex items-start text-green-700 rounded shadow-sm">
                            <CheckCircle className="w-5 h-5 mr-3 flex-shrink-0" />
                            <p className="text-sm font-medium">{success}</p>
                        </div>
                    )}

                    <div className="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden">
                        <div className="p-6 border-b flex items-center justify-between">
                            <h2 className="text-xl font-bold text-gray-900 flex items-center">
                                <School className="w-6 h-6 mr-2 text-blue-600" />
                                Professional & Location Details
                            </h2>
                            <span className="text-xs font-mono text-gray-400 bg-gray-50 px-2 py-1 rounded">
                                Ref: {teacher.employeeId || 'N/A'}
                            </span>
                        </div>

                        <form onSubmit={handleUpdate} className="p-8 space-y-8">
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-6 pb-6 border-b border-gray-50">
                                <div className="md:col-span-2">
                                    <label className="block text-sm font-semibold text-gray-500 mb-1">School Name</label>
                                    <p className="text-gray-900 font-bold bg-gray-50 p-3 rounded-lg border border-gray-100">
                                        {teacher.schoolName}
                                    </p>
                                </div>
                                <div>
                                    <label className="block text-sm font-semibold text-gray-500 mb-1">UDISE Code</label>
                                    <p className="text-gray-900 font-bold bg-gray-50 p-3 rounded-lg border border-gray-100 italic">
                                        {teacher.udiseCode || 'Not provided'}
                                    </p>
                                </div>
                                <div>
                                    <label className="block text-sm font-semibold text-gray-500 mb-1">Subject</label>
                                    <p className="text-gray-900 font-bold bg-gray-50 p-3 rounded-lg border border-gray-100">
                                        {teacher.subjectName || 'Maths (Default)'}
                                    </p>
                                </div>
                            </div>

                            {/* Editable Location Details */}
                            <div className="space-y-6">
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                                    <div>
                                        <h3 className="text-sm font-bold text-gray-600 flex items-center mb-4 uppercase tracking-wider">
                                            <MapPin className="w-4 h-4 mr-2" />
                                            Current Location
                                        </h3>
                                        <div className="space-y-4">
                                            <div>
                                                <label className="block text-xs font-bold text-gray-400 mb-1">District</label>
                                                <select
                                                    className="w-full px-4 py-2 bg-white border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                                                    value={teacher.currentLocation?.districtId || ''}
                                                    onChange={e => handleDistrictChange(e.target.value, 'current')}
                                                >
                                                    <option value="">Select District</option>
                                                    {districts.map(d => <option key={d.id} value={d.id}>{d.name}</option>)}
                                                </select>
                                            </div>
                                            <div>
                                                <label className="block text-xs font-bold text-gray-400 mb-1">Block</label>
                                                <select
                                                    className="w-full px-4 py-2 bg-white border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                                                    value={teacher.currentLocation?.blockId || ''}
                                                    onChange={e => setTeacher({
                                                        ...teacher,
                                                        currentLocation: {
                                                            ...(teacher.currentLocation || {}),
                                                            blockId: parseInt(e.target.value)
                                                        }
                                                    })}
                                                >
                                                    <option value="">Select Block</option>
                                                    {currentBlocks.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
                                                </select>
                                            </div>
                                        </div>
                                    </div>

                                    <div>
                                        <h3 className="text-sm font-bold text-gray-600 flex items-center mb-4 uppercase tracking-wider">
                                            <Save className="w-4 h-4 mr-2" />
                                            Transfer Preference
                                        </h3>
                                        <div className="space-y-4">
                                            <div>
                                                <label className="block text-xs font-bold text-gray-400 mb-1">Target District</label>
                                                <select
                                                    className="w-full px-4 py-2 bg-white border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                                                    value={teacher.preferredLocation?.districtId || ''}
                                                    onChange={e => handleDistrictChange(e.target.value, 'preferred')}
                                                >
                                                    <option value="">Select District</option>
                                                    {districts.map(d => <option key={d.id} value={d.id}>{d.name}</option>)}
                                                </select>
                                            </div>
                                            <div>
                                                <label className="block text-xs font-bold text-gray-400 mb-1">Target Block</label>
                                                <select
                                                    className="w-full px-4 py-2 bg-white border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                                                    value={teacher.preferredLocation?.blockId || ''}
                                                    onChange={e => setTeacher({
                                                        ...teacher,
                                                        preferredLocation: {
                                                            ...(teacher.preferredLocation || {}),
                                                            blockId: parseInt(e.target.value)
                                                        }
                                                    })}
                                                >
                                                    <option value="">Select Block</option>
                                                    {preferredBlocks.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
                                                </select>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div className="pt-6">
                                    <label className="block text-sm font-semibold text-gray-600 mb-2">Search Radius (KM)</label>
                                    <div className="flex items-center gap-4">
                                        <input
                                            type="range"
                                            min="5"
                                            max="100"
                                            step="5"
                                            className="flex-1 h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-blue-600"
                                            value={teacher.radiusKm}
                                            onChange={e => setTeacher({ ...teacher, radiusKm: parseInt(e.target.value) })}
                                        />
                                        <span className="w-12 text-center font-bold text-blue-600 bg-blue-50 py-1 rounded">
                                            {teacher.radiusKm}
                                        </span>
                                    </div>
                                </div>
                            </div>

                            <div className="pt-8 border-t border-gray-50">
                                <button
                                    type="submit"
                                    disabled={updating}
                                    className="w-full py-4 px-6 bg-blue-600 hover:bg-blue-700 text-white font-bold text-lg rounded-xl transition-all shadow-lg hover:shadow-xl active:scale-[0.98] disabled:opacity-50 flex items-center justify-center gap-3"
                                >
                                    {updating ? (
                                        <>
                                            <Loader2 className="w-6 h-6 animate-spin" />
                                            Saving Changes...
                                        </>
                                    ) : (
                                        <>
                                            <Save className="w-5 h-5" />
                                            Update Preferences
                                        </>
                                    )}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
}
