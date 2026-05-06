import { getToken } from 'next-auth/jwt';
import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

const secret = process.env.NEXTAUTH_SECRET!;

export async function POST(req: NextRequest) {
    const token = await getToken({ req, secret });
    if (!token) {
        return NextResponse.json({ error: 'Unauthorized' }, { status: 401 });
    }

    token.isOnboarded = true;

    const headers = new Headers();
    const { encode } = await import('next-auth/jwt');
    const newToken = await encode({ token, secret });

    headers.set('Set-Cookie', getCookieString(newToken));

    return NextResponse.json({ success: true }, { headers });
}

function getCookieString(token: string): string {
    const maxAge = 30 * 24 * 60 * 60; // 30 days
    return `next-auth.session-token=${token}; Path=/; HttpOnly; SameSite=Lax; Max-Age=${maxAge}`;
}
