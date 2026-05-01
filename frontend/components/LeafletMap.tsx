'use client';

import { useEffect, useRef } from 'react';

interface LeafletMapProps {
    selectedDistrictId: number | null;
    selectedBlockId: number | null;
    selectedDistrict: { id: number; name: string; lat?: number; lng?: number } | undefined;
    selectedBlock: { id: number; name: string; lat?: number; lng?: number } | undefined;
    lat: number | null;
    lng: number | null;
    onLocationChange: (lat: number, lng: number) => void;
}

const BIHAR_CENTER: [number, number] = [25.6, 85.1];
const DEFAULT_ZOOM = 7;
const BLOCK_ZOOM = 12;
const DISTRICT_ZOOM = 10;

export default function LeafletMap({
    selectedDistrict,
    selectedBlock,
    lat,
    lng,
    onLocationChange,
}: LeafletMapProps) {
    const containerRef = useRef<HTMLDivElement>(null);
    const mapRef = useRef<any>(null);
    const markerRef = useRef<any>(null);
    const initializedRef = useRef(false);
    const prevCenterKeyRef = useRef('');

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

    useEffect(() => {
        if (!containerRef.current || initializedRef.current) return;
        initializedRef.current = true;

        let cancelled = false;

        (async () => {
            const L = (await import('leaflet')).default || (await import('leaflet'));

            if (cancelled || !containerRef.current) return;

            const link = document.createElement('link');
            link.rel = 'stylesheet';
            link.href = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.css';
            document.head.appendChild(link);

            const icon = L.icon({
                iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
                iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
                shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
                iconSize: [25, 41],
                iconAnchor: [12, 41],
            });

            const map = L.map(containerRef.current, {
                center: mapCenter,
                zoom: mapZoom,
                scrollWheelZoom: true,
            });

            L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '&copy; OpenStreetMap',
            }).addTo(map);

            const marker = L.marker(mapCenter, { draggable: true, icon }).addTo(map);
            marker.on('dragend', () => {
                const pos = marker.getLatLng();
                onLocationChange(pos.lat, pos.lng);
            });

            mapRef.current = map;
            markerRef.current = marker;
            prevCenterKeyRef.current = `${mapCenter[0]},${mapCenter[1]},${mapZoom}`;
        })();

        return () => {
            cancelled = true;
            initializedRef.current = false;
            if (mapRef.current) {
                mapRef.current.remove();
                mapRef.current = null;
                markerRef.current = null;
            }
        };
    }, []);

    useEffect(() => {
        const key = `${mapCenter[0]},${mapCenter[1]},${mapZoom}`;
        if (key === prevCenterKeyRef.current) return;
        prevCenterKeyRef.current = key;

        const map = mapRef.current;
        const marker = markerRef.current;
        if (!map) return;

        map.setView(mapCenter, mapZoom, { animate: true });
        if (marker) marker.setLatLng(mapCenter);
    }, [mapCenter, mapZoom]);

    useEffect(() => {
        if (markerRef.current && lat != null && lng != null) {
            markerRef.current.setLatLng([lat, lng]);
        }
    }, [lat, lng]);

    return <div ref={containerRef} style={{ width: '100%', height: '100%' }} />;
}
