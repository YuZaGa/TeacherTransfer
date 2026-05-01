'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import api from '@/lib/api';
import LocationPicker, { District, Block } from '@/components/LocationPicker';
import { User, School, MapPin, Phone, Save, AlertCircle, Loader2 } from 'lucide-react';

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

export default function OnboardingPage() {
    const router = useRouter();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const [phoneError, setPhoneError] = useState('');

    const [formData, setFormData] = useState({
        phone: '',
        gender: 'MALE',
        employeeId: '',
        udiseCode: '',
        schoolName: '',
        subject: 'HINDI',
        schoolType: 'PRIMARY',
        currentDistrictId: null as number | null,
        currentBlockId: null as number | null,
        currentLat: null as number | null,
        currentLng: null as number | null,
        preferredDistrictId: null as number | null,
        preferredBlockId: null as number | null,
        preferredLat: null as number | null,
        preferredLng: null as number | null,
        radiusKm: '30',
    });

    const [districts, setDistricts] = useState<District[]>([]);
    const [currentBlocks, setCurrentBlocks] = useState<Block[]>([]);
    const [preferredBlocks, setPreferredBlocks] = useState<Block[]>([]);

    useEffect(() => {
        const fetchDistricts = async () => {
            try {
                const res = await api.get('/geography/districts');
                setDistricts(res.data.data || []);
            } catch (err) {
                console.error("Failed to fetch districts", err);
            }
        };
        fetchDistricts();
    }, []);

    const handleCurrentDistrictChange = async (districtId: number) => {
        setFormData({ ...formData, currentDistrictId: districtId, currentBlockId: null, currentLat: null, currentLng: null });
        setCurrentBlocks([]);
        try {
            const res = await api.get(`/geography/districts/${districtId}/blocks`);
            setCurrentBlocks(res.data.data || []);
        } catch (err) {
            console.error("Failed to fetch blocks", err);
        }
    };

    const handlePreferredDistrictChange = async (districtId: number) => {
        setFormData({ ...formData, preferredDistrictId: districtId, preferredBlockId: null, preferredLat: null, preferredLng: null });
        setPreferredBlocks([]);
        try {
            const res = await api.get(`/geography/districts/${districtId}/blocks`);
            setPreferredBlocks(res.data.data || []);
        } catch (err) {
            console.error("Failed to fetch blocks", err);
        }
    };

    const handleCurrentBlockChange = (blockId: number) => {
        const block = currentBlocks.find(b => b.id === blockId);
        setFormData({
            ...formData,
            currentBlockId: blockId,
            currentLat: block?.lat || null,
            currentLng: block?.lng || null,
        });
    };

    const handlePreferredBlockChange = (blockId: number) => {
        const block = preferredBlocks.find(b => b.id === blockId);
        setFormData({
            ...formData,
            preferredBlockId: blockId,
            preferredLat: block?.lat || null,
            preferredLng: block?.lng || null,
        });
    };

    const handleCurrentLocationMapChange = (lat: number, lng: number) => {
        setFormData({ ...formData, currentLat: lat, currentLng: lng });
    };

    const handlePreferredLocationMapChange = (lat: number, lng: number) => {
        setFormData({ ...formData, preferredLat: lat, preferredLng: lng });
    };

    const validatePhone = (phone: string) => {
        if (!phone) {
            setPhoneError('Phone number is required');
            return false;
        }
        if (!/^[6-9]\d{9}$/.test(phone)) {
            setPhoneError('Enter a valid 10-digit Indian mobile number (starting with 6-9)');
            return false;
        }
        setPhoneError('');
        return true;
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');

        if (!validatePhone(formData.phone)) return;
        if (!formData.schoolName.trim()) {
            setError('School name is required');
            return;
        }

        setLoading(true);

        try {
            const res = await fetch('/api/onboarding', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(formData),
            });

            const data = await res.json();

            if (!res.ok) {
                setError(data.error || 'Failed to save. Please try again.');
                return;
            }

            router.push('/dashboard');
            router.refresh();
        } catch {
            setError('Network error. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-3xl mx-auto p-6 min-h-screen flex items-center">
            <div className="w-full bg-white rounded-2xl shadow-xl overflow-hidden border border-gray-100">
                <div className="bg-gradient-to-r from-blue-600 to-indigo-600 p-8 text-white relative overflow-hidden">
                    <div className="relative z-10">
                        <h1 className="text-3xl font-bold">Welcome to TeacherTransfer!</h1>
                        <p className="mt-2 text-blue-100 opacity-90">
                            Tell us about your current posting so we can find your best matches.
                        </p>
                    </div>
                    <div className="absolute top-6 right-6 text-white opacity-10">
                        <User size={100} />
                    </div>
                </div>

                <form onSubmit={handleSubmit} className="p-8 space-y-10">
                    {error && (
                        <div className="p-4 bg-red-50 border-l-4 border-red-500 flex items-start text-red-700 rounded-r-lg">
                            <AlertCircle className="w-5 h-5 mr-3 flex-shrink-0 mt-0.5" />
                            <p className="text-sm font-medium">{error}</p>
                        </div>
                    )}

                    <section className="space-y-5">
                        <h2 className="flex items-center text-lg font-bold text-gray-900 pb-2 border-b border-gray-200">
                            <User className="w-5 h-5 mr-2 text-blue-600" />
                            Personal Info
                        </h2>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                            <div>
                                <label className="block text-sm font-semibold text-gray-700 mb-1.5">
                                    <span className="flex items-center gap-1">
                                        <Phone className="w-3.5 h-3.5" /> Phone Number *
                                    </span>
                                </label>
                                <div className="flex">
                                    <span className="inline-flex items-center px-3 bg-gray-100 border border-r-0 border-gray-200 rounded-l-lg text-gray-600 text-sm font-medium">
                                        +91
                                    </span>
                                    <input
                                        type="tel"
                                        required
                                        maxLength={10}
                                        placeholder="10-digit mobile"
                                        className={`w-full px-4 py-2.5 bg-gray-50 border rounded-r-lg focus:ring-2 focus:ring-blue-500 outline-none transition-all ${
                                            phoneError ? 'border-red-400' : 'border-gray-200'
                                        }`}
                                        value={formData.phone}
                                        onChange={e => {
                                            const val = e.target.value.replace(/\D/g, '').slice(0, 10);
                                            setFormData({ ...formData, phone: val });
                                            if (val.length === 10) validatePhone(val);
                                            else setPhoneError('');
                                        }}
                                        onBlur={() => formData.phone && validatePhone(formData.phone)}
                                    />
                                </div>
                                {phoneError && <p className="text-xs text-red-500 mt-1">{phoneError}</p>}
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-gray-700 mb-1.5">Gender</label>
                                <select
                                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                                    value={formData.gender}
                                    onChange={e => setFormData({ ...formData, gender: e.target.value })}
                                >
                                    <option value="MALE">Male</option>
                                    <option value="FEMALE">Female</option>
                                    <option value="OTHER">Other</option>
                                </select>
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-gray-700 mb-1.5">Employee ID</label>
                                <input
                                    type="text"
                                    placeholder="Bihar govt employee code"
                                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                                    value={formData.employeeId}
                                    onChange={e => setFormData({ ...formData, employeeId: e.target.value })}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-gray-700 mb-1.5">UDISE Code</label>
                                <input
                                    type="text"
                                    placeholder="11-digit school code"
                                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                                    value={formData.udiseCode}
                                    onChange={e => setFormData({ ...formData, udiseCode: e.target.value })}
                                />
                            </div>
                        </div>
                    </section>

                    <section className="space-y-5">
                        <h2 className="flex items-center text-lg font-bold text-gray-900 pb-2 border-b border-gray-200">
                            <School className="w-5 h-5 mr-2 text-blue-600" />
                            School Details
                        </h2>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
                            <div className="md:col-span-2">
                                <label className="block text-sm font-semibold text-gray-700 mb-1.5">Current School Name *</label>
                                <input
                                    type="text"
                                    required
                                    placeholder="Full name of your current school"
                                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                                    value={formData.schoolName}
                                    onChange={e => setFormData({ ...formData, schoolName: e.target.value })}
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-gray-700 mb-1.5">Subject</label>
                                <select
                                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                                    value={formData.subject}
                                    onChange={e => setFormData({ ...formData, subject: e.target.value })}
                                >
                                    {SUBJECTS.map(s => <option key={s.value} value={s.value}>{s.label}</option>)}
                                </select>
                            </div>
                            <div>
                                <label className="block text-sm font-semibold text-gray-700 mb-1.5">School Type</label>
                                <select
                                    className="w-full px-4 py-2.5 bg-gray-50 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                                    value={formData.schoolType}
                                    onChange={e => setFormData({ ...formData, schoolType: e.target.value })}
                                >
                                    {SCHOOL_TYPES.map(s => <option key={s.value} value={s.value}>{s.label}</option>)}
                                </select>
                            </div>
                        </div>
                    </section>

                    <section className="space-y-6">
                        <h2 className="flex items-center text-lg font-bold text-gray-900 pb-2 border-b border-gray-200">
                            <MapPin className="w-5 h-5 mr-2 text-blue-600" />
                            Location Preferences
                        </h2>

                        <LocationPicker
                            label="Current Location"
                            hint="Where you are currently posted. Select your block and adjust the pin to your school."
                            districts={districts}
                            blocks={currentBlocks}
                            selectedDistrictId={formData.currentDistrictId}
                            selectedBlockId={formData.currentBlockId}
                            lat={formData.currentLat}
                            lng={formData.currentLng}
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
                            selectedDistrictId={formData.preferredDistrictId}
                            selectedBlockId={formData.preferredBlockId}
                            lat={formData.preferredLat}
                            lng={formData.preferredLng}
                            mapHeight="h-56"
                            onDistrictChange={handlePreferredDistrictChange}
                            onBlockChange={handlePreferredBlockChange}
                            onLocationChange={handlePreferredLocationMapChange}
                        />

                        <div>
                            <label className="block text-sm font-semibold text-gray-700 mb-2">
                                Search Radius: {formData.radiusKm} km
                            </label>
                            <div className="flex items-center gap-4">
                                <input
                                    type="range"
                                    min="5"
                                    max="100"
                                    step="5"
                                    className="flex-1 h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-blue-600"
                                    value={formData.radiusKm}
                                    onChange={e => setFormData({ ...formData, radiusKm: e.target.value })}
                                />
                                <span className="w-12 text-center font-bold text-blue-600 bg-blue-50 py-1 rounded">
                                    {formData.radiusKm}
                                </span>
                            </div>
                            <p className="text-xs text-gray-400 mt-2">
                                Matches will be found within this distance from your preferred location.
                            </p>
                        </div>
                    </section>

                    <div className="pt-4">
                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full py-4 px-6 bg-gradient-to-r from-blue-600 to-indigo-600 hover:from-blue-700 hover:to-indigo-700 text-white font-bold text-lg rounded-xl transition-all shadow-lg hover:shadow-xl active:scale-[0.98] disabled:opacity-50 flex items-center justify-center gap-2"
                        >
                            {loading ? (
                                <>
                                    <Loader2 className="w-6 h-6 animate-spin" />
                                    Saving...
                                </>
                            ) : (
                                <>
                                    <Save className="w-5 h-5" />
                                    Complete Setup & Go to Dashboard
                                </>
                            )}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
