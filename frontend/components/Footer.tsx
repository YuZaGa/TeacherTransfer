import Link from 'next/link';
import { ArrowRightLeft } from 'lucide-react';

export default function Footer() {
    return (
        <footer className="bg-white border-t border-gray-200 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-7xl mx-auto flex flex-col md:flex-row justify-between items-center gap-6">
                <div className="flex items-center gap-2">
                    <ArrowRightLeft className="text-blue-600 w-5 h-5" />
                    <span className="font-bold text-gray-900">TeacherTransfer</span>
                </div>
                <div className="flex gap-6 text-sm font-medium text-gray-500">
                    <Link href="#" className="hover:text-blue-600">About Us</Link>
                    <Link href="#" className="hover:text-blue-600">FAQ</Link>
                    <Link href="#" className="hover:text-blue-600">Privacy Policy</Link>
                    <Link href="#" className="hover:text-blue-600">Terms of Service</Link>
                </div>
                <div className="text-sm text-gray-400">
                    &copy; {new Date().getFullYear()} TeacherTransfer. All rights reserved.
                </div>
            </div>
        </footer>
    );
}
