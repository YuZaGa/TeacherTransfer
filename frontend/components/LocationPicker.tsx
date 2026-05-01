'use client';

import { useState, useEffect } from 'react';
import { MapPin } from 'lucide-react';

export interface District {
    id: number;
    name: string;
    lat?: number;
    lng?: number;
}

export interface Block {
    id: number;
    name: string;
    lat?: number;
    lng?: number;
}

interface LocationPickerProps {
    label: string;
    hint: string;
    districts: District[];
    blocks: Block[];
    selectedDistrictId: number | null;
    selectedBlockId: number | null;
    lat: number | null;
    lng: number | null;
    mapHeight?: string;
    onDistrictChange: (districtId: number) => void;
    onBlockChange: (blockId: number) => void;
    onLocationChange: (lat: number, lng: number) => void;
}

export default function LocationPicker(props: LocationPickerProps) {
    const [MapComponent, setMapComponent] = useState<any>(null);

    useEffect(() => {
        import('./LeafletMap').then(mod => setMapComponent(() => mod.default));
    }, []);

    const {
        label, hint, districts, blocks,
        selectedDistrictId, selectedBlockId,
        lat, lng, mapHeight = 'h-56',
        onDistrictChange, onBlockChange, onLocationChange,
    } = props;

    const selectedDistrict = districts.find(d => d.id === selectedDistrictId);
    const selectedBlock = blocks.find(b => b.id === selectedBlockId);

    const locationLabel = selectedBlock && selectedDistrict
        ? `Near ${selectedBlock.name}, ${selectedDistrict.name}`
        : selectedDistrict
            ? selectedDistrict.name + ' district'
            : null;

    return (
        <div className="space-y-3">
            <h3 className="text-sm font-bold text-gray-600 flex items-center uppercase tracking-wider">
                <MapPin className="w-4 h-4 mr-2" />
                {label}
            </h3>
            <p className="text-xs text-gray-400">{hint}</p>

            <div className="grid grid-cols-2 gap-3">
                <div>
                    <label className="block text-xs font-bold text-gray-400 mb-1">District</label>
                    <select
                        className="w-full px-3 py-2 bg-white border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-sm"
                        value={selectedDistrictId || ''}
                        onChange={e => { const v = e.target.value; if (v) onDistrictChange(parseInt(v)); }}
                    >
                        <option value="">Select District</option>
                        {districts.map(d => <option key={d.id} value={d.id}>{d.name}</option>)}
                    </select>
                </div>
                <div>
                    <label className="block text-xs font-bold text-gray-400 mb-1">Block</label>
                    <select
                        className="w-full px-3 py-2 bg-white border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-sm"
                        value={selectedBlockId || ''}
                        onChange={e => { const v = e.target.value; if (v) onBlockChange(parseInt(v)); }}
                    >
                        <option value="">Select Block</option>
                        {blocks.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
                    </select>
                </div>
            </div>

            <div className={`rounded-xl overflow-hidden border border-gray-200 ${mapHeight} relative bg-gray-100`}>
                {MapComponent ? (
                    <MapComponent
                        selectedDistrictId={selectedDistrictId}
                        selectedBlockId={selectedBlockId}
                        selectedDistrict={selectedDistrict}
                        selectedBlock={selectedBlock}
                        lat={lat}
                        lng={lng}
                        onLocationChange={onLocationChange}
                    />
                ) : (
                    <div className="w-full h-full flex items-center justify-center text-gray-400 text-sm">
                        Loading map...
                    </div>
                )}
            </div>

            {locationLabel && (
                <p className="text-xs text-gray-500 flex items-center gap-1">
                    <MapPin className="w-3 h-3" />
                    Selected: {locationLabel}
                </p>
            )}
        </div>
    );
}
