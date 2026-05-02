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

        const payload = {
            name: body.name,
            phone: body.phone,
            gender: body.gender,
            employeeId: body.employeeId || null,
            udiseCode: body.udiseCode || null,
            schoolName: body.schoolName,
            subject: body.subject,
            schoolType: body.schoolType,
            currentDistrictId: body.currentDistrictId ? parseInt(body.currentDistrictId) : null,
            currentBlockId: body.currentBlockId ? parseInt(body.currentBlockId) : null,
            currentLat: body.currentLat ? parseFloat(body.currentLat) : null,
            currentLng: body.currentLng ? parseFloat(body.currentLng) : null,
            preferredDistrictId: body.preferredDistrictId ? parseInt(body.preferredDistrictId) : null,
            preferredBlockId: body.preferredBlockId ? parseInt(body.preferredBlockId) : null,
            preferredLat: body.preferredLat ? parseFloat(body.preferredLat) : null,
            preferredLng: body.preferredLng ? parseFloat(body.preferredLng) : null,
            radiusKm: body.radiusKm ? parseInt(body.radiusKm) : 30,
        };

        const backendToken = (session.user as any).backendToken;
        const res = await fetch(`${API_URL}/auth/onboarding/complete`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${backendToken}`,
            },
            body: JSON.stringify(payload),
        });

        const data = await res.json();

        if (!res.ok || !data.success) {
            console.error('Backend onboarding error:', res.status, data);
            return NextResponse.json(
                { error: data.message || 'Failed to save onboarding data' },
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
