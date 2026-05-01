'use client';

import { useState, useEffect, useCallback, useRef } from 'react';
import dynamic from 'next/dynamic';
import { MapPin } from 'lucide-react';

const MapContainer = dynamic(
    () => import('react-leaflet').then(mod => mod.MapContainer),
    { ssr: false }
);
const TileLayer = dynamic(
    () => import('react-leaflet').then(mod => mod.TileLayer),
    { ssr: false }
);

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

const BIHAR_CENTER: [number, number] = [25.6, 85.1];
const DEFAULT_ZOOM = 7;
const BLOCK_ZOOM = 12;
const DISTRICT_ZOOM = 10;

function DraggableMarker({
    position,
    onMove,
}: {
    position: [number, number];
    onMove: (lat: number, lng: number) => void;
}) {
    const markerRef = useRef<any>(null);

    const handleDragEnd = useCallback(() => {
        const marker = markerRef.current;
        if (marker) {
            const pos = marker.getLatLng();
            onMove(pos.lat, pos.lng);
        }
    }, [onMove]);

    useEffect(() => {
        const marker = markerRef.current;
        if (marker) {
            marker.setLatLng(position);
        }
    }, [position]);

    return null;
}

function MapEvents({ center, zoom }: { center: [number, number]; zoom: number }) {
    const mapRef = useRef<any>(null);
    const prevCenterRef = useRef<string>('');

    useEffect(() => {
        const key = `${center[0]},${center[1]},${zoom}`;
        if (key === prevCenterRef.current) return;
        prevCenterRef.current = key;

        const checkMap = () => {
            const el = document.querySelector('.leaflet-container');
            if (el && (el as any)._leafletMap) {
                (el as any)._leafletMap.setView(center, zoom);
            }
        };

        const timer = setTimeout(checkMap, 100);
        return () => clearTimeout(timer);
    }, [center, zoom]);

    return null;
}

export default function LocationPicker({
    label,
    hint,
    districts,
    blocks,
    selectedDistrictId,
    selectedBlockId,
    lat,
    lng,
    mapHeight = 'h-56',
    onDistrictChange,
    onBlockChange,
    onLocationChange,
}: LocationPickerProps) {
    const [mounted, setMounted] = useState(false);
    const [leafletMod, setLeafletMod] = useState<any>(null);
    const [markerMod, setMarkerMod] = useState<any>(null);
    const [customIcon, setCustomIcon] = useState<any>(null);

    useEffect(() => {
        setMounted(true);
        Promise.all([
            import('react-leaflet'),
            import('leaflet'),
        ]).then(([rl, L]) => {
            setLeafletMod(rl);
            setMarkerMod(rl);
            setCustomIcon(L.default.icon({
                iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
                iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
                shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
                iconSize: [25, 41],
                iconAnchor: [12, 41],
            }));
        });
    }, []);

    const selectedDistrict = districts.find(d => d.id === selectedDistrictId);
    const selectedBlock = blocks.find(b => b.id === selectedBlockId);

    const markerPos: [number, number] | null =
        lat != null && lng != null ? [lat, lng] : null;

    const mapCenter: [number, number] = markerPos
        || (selectedBlock?.lat && selectedBlock?.lng ? [selectedBlock.lat, selectedBlock.lng] : null)
        || (selectedDistrict?.lat && selectedDistrict?.lng ? [selectedDistrict.lat, selectedDistrict.lng] : null)
        || BIHAR_CENTER;

    const mapZoom = markerPos ? BLOCK_ZOOM
        : selectedBlock ? BLOCK_ZOOM
        : selectedDistrict ? DISTRICT_ZOOM
        : DEFAULT_ZOOM;

    const handleDistrictChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const val = e.target.value;
        if (val) onDistrictChange(parseInt(val));
    };

    const handleBlockChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        const val = e.target.value;
        if (val) onBlockChange(parseInt(val));
    };

    const locationLabel = selectedBlock && selectedDistrict
        ? `Near ${selectedBlock.name}, ${selectedDistrict.name}`
        : selectedDistrict
            ? selectedDistrict.name + ' district'
            : null;

    const LMarker = markerMod?.Marker;
    const LMapContainer = leafletMod?.MapContainer;
    const LTileLayer = leafletMod?.TileLayer;

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
                        onChange={handleDistrictChange}
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
                        onChange={handleBlockChange}
                    >
                        <option value="">Select Block</option>
                        {blocks.map(b => <option key={b.id} value={b.id}>{b.name}</option>)}
                    </select>
                </div>
            </div>

            <div className={`rounded-xl overflow-hidden border border-gray-200 ${mapHeight} relative bg-gray-100`}>
                {mounted && LMapContainer && LTileLayer && customIcon ? (
                    <>
                        <link
                            rel="stylesheet"
                            href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"
                            crossOrigin=""
                        />
                        <LMapContainer
                            center={mapCenter}
                            zoom={mapZoom}
                            style={{ height: '100%', width: '100%' }}
                            scrollWheelZoom={true}
                        >
                            <LTileLayer
                                attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OSM</a>'
                                url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                            />
                            {markerPos && LMarker && (
                                <LMarker
                                    position={markerPos}
                                    draggable={true}
                                    icon={customIcon}
                                    eventHandlers={{
                                        dragend: (e: any) => {
                                            const pos = e.target.getLatLng();
                                            onLocationChange(pos.lat, pos.lng);
                                        },
                                    }}
                                />
                            )}
                            <MapEvents center={mapCenter} zoom={mapZoom} />
                        </LMapContainer>
                    </>
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
