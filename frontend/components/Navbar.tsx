'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { User, LogIn, UserPlus, Home, Map } from 'lucide-react';
import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs));
}

export default function Navbar() {
    const pathname = usePathname();

    const navItems = [
        { name: 'Home', href: '/', icon: Home },
        { name: 'Matches', href: '/matches', icon: Map },
        { name: 'Profile', href: '/profile', icon: User },
    ];

    return (
        <nav className="bg-white border-b border-gray-200 sticky top-0 z-50">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <div className="flex justify-between h-16">
                    <div className="flex items-center">
                        <Link href="/" className="flex-shrink-0 flex items-center">
                            <span className="text-2xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">
                                TeacherTransfer
                            </span>
                        </Link>
                        <div className="hidden sm:ml-8 sm:flex sm:space-x-4">
                            {navItems.map((item) => {
                                const isActive = pathname === item.href;
                                return (
                                    <Link
                                        key={item.href}
                                        href={item.href}
                                        className={cn(
                                            'inline-flex items-center px-3 py-2 text-sm font-medium rounded-md transition-colors',
                                            isActive
                                                ? 'text-blue-600 bg-blue-50'
                                                : 'text-gray-600 hover:text-blue-600 hover:bg-gray-50'
                                        )}
                                    >
                                        <item.icon className="w-4 h-4 mr-2" />
                                        {item.name}
                                    </Link>
                                );
                            })}
                        </div>
                    </div>
                    <div className="flex items-center space-x-4">
                        {pathname !== '/login' && (
                            <>
                                <Link
                                    href="/login"
                                    className="text-gray-600 hover:text-blue-600 text-sm font-medium flex items-center"
                                >
                                    <LogIn className="w-4 h-4 mr-1" />
                                    Login
                                </Link>
                                <Link
                                    href="/auth/otp"
                                    className="bg-blue-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-blue-700 transition shadow-sm flex items-center"
                                >
                                    <UserPlus className="w-4 h-4 mr-1" />
                                    Register
                                </Link>
                            </>
                        )}
                    </div>
                </div>
            </div>
        </nav>
    );
}
