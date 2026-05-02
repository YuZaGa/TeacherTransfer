'use client';

import { useState } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useSession, signOut } from 'next-auth/react';
import { User, LogIn, UserPlus, Home, Map, LogOut, LayoutDashboard, ArrowRightLeft, Menu, X } from 'lucide-react';
import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs));
}

export default function Navbar() {
    const pathname = usePathname();
    const { data: session } = useSession();
    const isLoggedIn = !!session?.user;
    const [menuOpen, setMenuOpen] = useState(false);

    if (pathname === '/onboarding') return null;

    const hideActions = ['/login', '/auth/signup'].includes(pathname);

    const navItems = isLoggedIn
        ? [
              { name: 'Dashboard', href: '/dashboard', icon: LayoutDashboard },
              { name: 'Matches', href: '/matches', icon: Map },
              { name: 'Profile', href: '/profile', icon: User },
          ]
        : [
              { name: 'Home', href: '/', icon: Home },
          ];

    const isActive = (href: string) => pathname === href;

    return (
        <nav className="bg-white border-b border-gray-200 sticky top-0 z-50">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex justify-between h-16 items-center">
                    <div className="flex items-center gap-2">
                        <Link href={isLoggedIn ? '/dashboard' : '/'} className="flex items-center gap-2">
                            <div className="bg-blue-600 p-1.5 rounded-lg">
                                <ArrowRightLeft className="text-white w-5 h-5" />
                            </div>
                            <span className="text-xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">
                                TeacherTransfer
                            </span>
                        </Link>
                    </div>

                    <div className="flex items-center gap-1">
                        <div className="hidden sm:flex sm:items-center sm:space-x-1">
                            {navItems.map((item) => (
                                <Link
                                    key={item.href}
                                    href={item.href}
                                    className={cn(
                                        'inline-flex items-center px-3 py-2 text-sm font-medium rounded-md transition-colors',
                                        isActive(item.href)
                                            ? 'text-blue-600 bg-blue-50'
                                            : 'text-gray-600 hover:text-blue-600 hover:bg-gray-50'
                                    )}
                                >
                                    <item.icon className="w-4 h-4 mr-1.5" />
                                    {item.name}
                                </Link>
                            ))}
                            {isLoggedIn && (
                                <button
                                    onClick={() => signOut({ callbackUrl: '/login' })}
                                    className="inline-flex items-center px-3 py-2 text-sm font-medium text-gray-600 hover:text-red-600 hover:bg-gray-50 rounded-md transition-colors"
                                    title="Sign out"
                                >
                                    <LogOut className="w-4 h-4 mr-1.5" />
                                    Sign Out
                                </button>
                            )}
                            {!isLoggedIn && !hideActions && (
                                <>
                                    <Link
                                        href="/login"
                                        className="inline-flex items-center px-3 py-2 text-sm font-medium text-gray-600 hover:text-blue-600 hover:bg-gray-50 rounded-md transition-colors"
                                    >
                                        <LogIn className="w-4 h-4 mr-1.5" />
                                        Login
                                    </Link>
                                    <Link
                                        href="/auth/signup"
                                        className="inline-flex items-center px-3 py-2 text-sm font-medium bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
                                    >
                                        <UserPlus className="w-4 h-4 mr-1.5" />
                                        Register
                                    </Link>
                                </>
                            )}
                        </div>

                        <button
                            onClick={() => setMenuOpen(!menuOpen)}
                            className="sm:hidden p-2 text-gray-600 hover:text-blue-600 hover:bg-gray-50 rounded-lg transition-colors"
                            aria-label="Toggle menu"
                        >
                            {menuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
                        </button>
                    </div>
                </div>
            </div>

            {menuOpen && (
                <div className="sm:hidden border-t border-gray-100 bg-white px-4 py-3 space-y-1">
                    {navItems.map((item) => (
                        <Link
                            key={item.href}
                            href={item.href}
                            onClick={() => setMenuOpen(false)}
                            className={cn(
                                'flex items-center px-3 py-2.5 text-sm font-medium rounded-md transition-colors',
                                isActive(item.href)
                                    ? 'text-blue-600 bg-blue-50'
                                    : 'text-gray-600 hover:text-blue-600 hover:bg-gray-50'
                            )}
                        >
                            <item.icon className="w-4 h-4 mr-2" />
                            {item.name}
                        </Link>
                    ))}
                    {isLoggedIn ? (
                        <button
                            onClick={() => { setMenuOpen(false); signOut({ callbackUrl: '/login' }); }}
                            className="flex items-center w-full px-3 py-2.5 text-sm font-medium text-red-600 hover:bg-red-50 rounded-md transition-colors"
                        >
                            <LogOut className="w-4 h-4 mr-2" />
                            Sign Out
                        </button>
                    ) : !hideActions ? (
                        <>
                            <Link
                                href="/login"
                                onClick={() => setMenuOpen(false)}
                                className="flex items-center px-3 py-2.5 text-sm font-medium text-gray-600 hover:text-blue-600 hover:bg-gray-50 rounded-md transition-colors"
                            >
                                <LogIn className="w-4 h-4 mr-2" />
                                Login
                            </Link>
                            <Link
                                href="/auth/signup"
                                onClick={() => setMenuOpen(false)}
                                className="flex items-center px-3 py-2.5 text-sm font-medium text-blue-600 hover:bg-blue-50 rounded-md transition-colors"
                            >
                                <UserPlus className="w-4 h-4 mr-2" />
                                Register
                            </Link>
                        </>
                    ) : null}
                </div>
            )}
        </nav>
    );
}
