import { getServerSession } from 'next-auth/next';
import { authOptions } from '@/lib/auth';
import { NextResponse } from 'next/server';

const API_URL = process.env.BACKEND_API_URL || process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

export async function POST(req: Request) {
    const session = await getServerSession(authOptions);

    if (!session?.user) {
        return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });
    }

    try {
        const body = await req.json();

        const {
            phone,
            gender,
            employeeId,
            udiseCode,
            schoolName,
            subject,
            schoolType,
            currentDistrictId,
            currentBlockId,
            preferredDistrictId,
            preferredBlockId,
            radiusKm,
        } = body;

        // Validate phone: 10-digit Indian mobile
        if (!phone || !/^[6-9]\d{9}$/.test(phone)) {
            return NextResponse.json(
                { error: 'Please enter a valid 10-digit Indian mobile number' },
                { status: 400 }
            );
        }

        // Forward to backend API to update teacher profile
        const backendToken = (session.user as any).backendToken;
        const res = await fetch(`${API_URL}/teacher/me`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${backendToken}`,
            },
            body: JSON.stringify({
                phone,
                gender,
                employeeId: employeeId || null,
                udiseCode: udiseCode || null,
                schoolName,
                subject,
                schoolType,
                currentLocation: {
                    districtId: currentDistrictId ? parseInt(currentDistrictId) : null,
                    blockId: currentBlockId ? parseInt(currentBlockId) : null,
                },
                preferredLocation: {
                    districtId: preferredDistrictId ? parseInt(preferredDistrictId) : null,
                    blockId: preferredBlockId ? parseInt(preferredBlockId) : null,
                },
                radiusKm: radiusKm ? parseInt(radiusKm) : 30,
            }),
        });

        if (!res.ok) {
            const errorText = await res.text();
            console.error('Backend onboarding error:', res.status, errorText);
            return NextResponse.json(
                { error: 'Failed to save onboarding data' },
                { status: res.status }
            );
        }

        return NextResponse.json({ success: true, redirectTo: '/dashboard' });
    } catch (error: any) {
        console.error('Onboarding error:', error);
        return NextResponse.json(
            { error: 'Failed to save onboarding data' },
            { status: 500 }
        );
    }
}
