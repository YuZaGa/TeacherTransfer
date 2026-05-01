'use client';

import { useState, useEffect, useRef } from 'react';
import { useRouter } from 'next/navigation';
import { useSession, signOut } from 'next-auth/react';
import api from '@/lib/api';
import LocationPicker, { District, Block } from '@/components/LocationPicker';
import { User, School, MapPin, Save, AlertCircle, Loader2, LogOut, CheckCircle, CreditCard } from 'lucide-react';

const SUBJECTS = [
    { value: 'ACCOUNTANCY', label: 'Accountancy' },
    { value: 'AGRICULTURE', label: 'Agriculture' },
    { value: 'ARABIC', label: 'Arabic' },
    { value: 'AUTOMOTIVE', label: 'Automotive' },
    { value: 'BENGALI', label: 'Bangla / Bengali' },
    { value: 'BANKING_INSURANCE', label: 'Banking & Insurance' },
    { value: 'BEAUTY_WELLNESS', label: 'Beauty and Wellness' },
    { value: 'BIOLOGY', label: 'Biology' },
    { value: 'BHOJPURI', label: 'Bhojpuri' },
    { value: 'BOTANY', label: 'Botany' },
    { value: 'BUSINESS_STUDIES', label: 'Business Studies' },
    { value: 'CHEMISTRY', label: 'Chemistry' },
    { value: 'COMPUTER_SCIENCE', label: 'Computer Science' },
    { value: 'DANCE', label: 'Dance' },
    { value: 'DATA_SCIENCE', label: 'Data Science' },
    { value: 'ECONOMICS', label: 'Economics' },
    { value: 'ELECTRICAL_ELECTRONICS', label: 'Electrical & Electronics Technology' },
    { value: 'ENGLISH', label: 'English' },
    { value: 'ENTREPRENEURSHIP', label: 'Entrepreneurship' },
    { value: 'ENVIRONMENTAL_STUDIES', label: 'Environmental Studies (EVS)' },
    { value: 'FASHION_STUDIES', label: 'Fashion Studies' },
    { value: 'FINE_ARTS', label: 'Fine Arts / Painting' },
    { value: 'FOOD_PRODUCTION', label: 'Food Production' },
    { value: 'GEOGRAPHY', label: 'Geography' },
    { value: 'HEALTHCARE', label: 'Healthcare' },
    { value: 'HINDI', label: 'Hindi' },
    { value: 'HISTORY', label: 'History' },
    { value: 'HOME_SCIENCE', label: 'Home Science' },
    { value: 'HORTICULTURE', label: 'Horticulture & Floriculture' },
    { value: 'INFORMATICS_PRACTICES', label: 'Informatics Practices' },
    { value: 'LIBRARY_SCIENCE', label: 'Library and Information Science' },
    { value: 'MAGAHI', label: 'Magahi' },
    { value: 'MAITHILI', label: 'Maithili' },
    { value: 'MARKETING', label: 'Marketing' },
    { value: 'MASS_MEDIA', label: 'Mass Media Studies' },
    { value: 'MATHEMATICS', label: 'Mathematics' },
    { value: 'MULTIMEDIA_WEB_TECH', label: 'Multimedia & Web Technology' },
    { value: 'MUSIC', label: 'Music' },
    { value: 'PALI', label: 'Pali' },
    { value: 'PERSIAN', label: 'Persian / Farsi' },
    { value: 'PHILOSOPHY', label: 'Philosophy' },
    { value: 'PHYSICAL_EDUCATION', label: 'Physical Education & Yoga' },
    { value: 'PHYSICS', label: 'Physics' },
    { value: 'POLITICAL_SCIENCE', label: 'Political Science / Civics' },
    { value: 'PRAKRIT', label: 'Prakrit' },
    { value: 'PSYCHOLOGY', label: 'Psychology' },
    { value: 'RETAIL', label: 'Retail' },
    { value: 'SANSKRIT', label: 'Sanskrit' },
    { value: 'SOCIOLOGY', label: 'Sociology' },
    { value: 'STENOGRAPHY', label: 'Stenography / Shorthand (Hindi/English)' },
    { value: 'TAXATION', label: 'Taxation' },
    { value: 'TOURISM', label: 'Tourism' },
    { value: 'URDU', label: 'Urdu' },
    { value: 'ZOOLOGY', label: 'Zoology' },
];

const SCHOOL_TYPES = [
    { value: 'PRIMARY', label: 'Primary School (1-5)' },
    { value: 'MIDDLE', label: 'Middle School (6-8)' },
    { value: 'HIGH', label: 'High School (9-10)' },
    { value: 'PLUS_TWO', label: '+2 School (11-12)' },
];

export default function ProfilePage() {
    const router = useRouter();
    const { data: session, status: sessionStatus } = useSession();
    const [loading, setLoading] = useState(true);
    const [updating, setUpdating] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [teacher, setTeacher] = useState<any>(null);
    const [originalTeacher, setOriginalTeacher] = useState<any>(null);
    const [districts, setDistricts] = useState<District[]>([]);
    const [currentBlocks, setCurrentBlocks] = useState<Block[]>([]);
    const [preferredBlocks, setPreferredBlocks] = useState<Block[]>([]);
    const [showConfirmModal, setShowConfirmModal] = useState(false);

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
                setOriginalTeacher(JSON.parse(JSON.stringify(data)));

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

    const handleCurrentDistrictChange = async (districtId: number) => {
        try {
            const res = await api.get(`/geography/districts/${districtId}/blocks`);
            setCurrentBlocks(res.data.data);
            const dist = districts.find(d => d.id === districtId);
            setTeacher({
                ...teacher,
                currentLocation: {
                    districtId,
                    blockId: null,
                    lat: dist?.lat || null,
                    lng: dist?.lng || null,
                },
            });
        } catch (err) {
            setError('Failed to load blocks.');
        }
    };

    const handleCurrentBlockChange = (blockId: number) => {
        const block = currentBlocks.find(b => b.id === blockId);
        setTeacher({
            ...teacher,
            currentLocation: {
                ...(teacher.currentLocation || {}),
                blockId,
                lat: block?.lat || teacher.currentLocation?.lat,
                lng: block?.lng || teacher.currentLocation?.lng,
            },
        });
    };

    const handlePreferredDistrictChange = async (districtId: number) => {
        try {
            const res = await api.get(`/geography/districts/${districtId}/blocks`);
            setPreferredBlocks(res.data.data);
            const dist = districts.find(d => d.id === districtId);
            setTeacher({
                ...teacher,
                preferredLocation: {
                    districtId,
                    blockId: null,
                    lat: dist?.lat || null,
                    lng: dist?.lng || null,
                },
            });
        } catch (err) {
            setError('Failed to load blocks.');
        }
    };

    const handlePreferredBlockChange = (blockId: number) => {
        const block = preferredBlocks.find(b => b.id === blockId);
        setTeacher({
            ...teacher,
            preferredLocation: {
                ...(teacher.preferredLocation || {}),
                blockId,
                lat: block?.lat || teacher.preferredLocation?.lat,
                lng: block?.lng || teacher.preferredLocation?.lng,
            },
        });
    };

    const handleCurrentLocationMapChange = (lat: number, lng: number) => {
        setTeacher({
            ...teacher,
            currentLocation: { ...(teacher.currentLocation || {}), lat, lng },
        });
    };

    const handlePreferredLocationMapChange = (lat: number, lng: number) => {
        setTeacher({
            ...teacher,
            preferredLocation: { ...(teacher.preferredLocation || {}), lat, lng },
        });
    };

    const hasMatchAffectingChanges = (): boolean => {
        if (!originalTeacher) return false;
        const o = originalTeacher;
        const t = teacher;

        const locChanged = (locA: any, locB: any) => {
            if (!locA && !locB) return false;
            if (!locA || !locB) return true;
            return (
                locA.districtId !== locB.districtId ||
                locA.blockId !== locB.blockId ||
                locA.lat !== locB.lat ||
                locA.lng !== locB.lng
            );
        };

        return (
            locChanged(o.currentLocation, t.currentLocation) ||
            locChanged(o.preferredLocation, t.preferredLocation) ||
            o.radiusKm !== t.radiusKm ||
            JSON.stringify(o.subject) !== JSON.stringify(t.subject) ||
            JSON.stringify(o.schoolType) !== JSON.stringify(t.schoolType)
        );
    };

    const handleUpdateClick = (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setSuccess('');

        if (hasMatchAffectingChanges()) {
            setShowConfirmModal(true);
        } else {
            performUpdate();
        }
    };

    const performUpdate = async () => {
        setShowConfirmModal(false);
        setUpdating(true);
        try {
            await api.put('/teacher/me', teacher);
            setSuccess('Profile updated successfully!');
            setOriginalTeacher(JSON.parse(JSON.stringify(teacher)));
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

                        <form onSubmit={handleUpdateClick} className="p-8 space-y-8">
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
                                    <select
                                        className="w-full px-4 py-2 bg-white border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-sm"
                                        value={teacher.subject || ''}
                                        onChange={e => setTeacher({ ...teacher, subject: e.target.value || null })}
                                    >
                                        <option value="">Select Subject</option>
                                        {SUBJECTS.map(s => <option key={s.value} value={s.value}>{s.label}</option>)}
                                    </select>
                                </div>
                                <div>
                                    <label className="block text-sm font-semibold text-gray-500 mb-1">School Level</label>
                                    <select
                                        className="w-full px-4 py-2 bg-white border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-sm"
                                        value={teacher.schoolType || ''}
                                        onChange={e => setTeacher({ ...teacher, schoolType: e.target.value || null })}
                                    >
                                        <option value="">Select School Level</option>
                                        {SCHOOL_TYPES.map(s => <option key={s.value} value={s.value}>{s.label}</option>)}
                                    </select>
                                </div>
                            </div>

                            <LocationPicker
                                label="Current Location"
                                hint="Where you are currently posted. Adjust the pin to your school location."
                                districts={districts}
                                blocks={currentBlocks}
                                selectedDistrictId={teacher.currentLocation?.districtId || null}
                                selectedBlockId={teacher.currentLocation?.blockId || null}
                                lat={teacher.currentLocation?.lat || null}
                                lng={teacher.currentLocation?.lng || null}
                                mapHeight="h-48"
                                onDistrictChange={handleCurrentDistrictChange}
                                onBlockChange={handleCurrentBlockChange}
                                onLocationChange={handleCurrentLocationMapChange}
                            />

                            <LocationPicker
                                label="Preferred Transfer Location"
                                hint="Where you want to transfer. Matches will be shown based on this location."
                                districts={districts}
                                blocks={preferredBlocks}
                                selectedDistrictId={teacher.preferredLocation?.districtId || null}
                                selectedBlockId={teacher.preferredLocation?.blockId || null}
                                lat={teacher.preferredLocation?.lat || null}
                                lng={teacher.preferredLocation?.lng || null}
                                mapHeight="h-64"
                                onDistrictChange={handlePreferredDistrictChange}
                                onBlockChange={handlePreferredBlockChange}
                                onLocationChange={handlePreferredLocationMapChange}
                            />

                            <div className="pt-4">
                                <label className="block text-sm font-semibold text-gray-600 mb-2">
                                    Search Radius: {teacher.radiusKm} km
                                </label>
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
                                <p className="text-xs text-gray-400 mt-2">
                                    Matches will be found within this distance from your preferred location.
                                </p>
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

            {showConfirmModal && (
                <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/50">
                    <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full mx-4 p-8">
                        <div className="flex items-center gap-3 mb-4">
                            <div className="w-12 h-12 bg-amber-100 rounded-full flex items-center justify-center">
                                <AlertCircle className="w-6 h-6 text-amber-600" />
                            </div>
                            <h3 className="text-lg font-bold text-gray-900">Confirm Update</h3>
                        </div>
                        <p className="text-gray-600 mb-6">
                            Changing your location, subject, school type, or search radius will reset your current matches.
                            You will need to re-discover teachers after the update. Your existing interest requests are preserved.
                        </p>
                        <div className="flex gap-3">
                            <button
                                onClick={() => setShowConfirmModal(false)}
                                className="flex-1 py-3 px-4 bg-gray-100 text-gray-700 font-bold rounded-xl hover:bg-gray-200 transition-colors"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={performUpdate}
                                className="flex-1 py-3 px-4 bg-blue-600 text-white font-bold rounded-xl hover:bg-blue-700 transition-colors"
                            >
                                Confirm Update
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
