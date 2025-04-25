// Set your Mapbox access token
mapboxgl.accessToken = 'pk.eyJ1Ijoib2xhbCIsImEiOiJjbThidjM5aTgxcDNtMm1zNTh6NjRqeDE3In0.EIobbUYeWUFMarhuxGr_sg';

// Initialize the map centered on a default location
var map = new mapboxgl.Map({
    container: 'map', // ID of the map container
    style: 'mapbox://styles/mapbox/streets-v10',
    center: [36.860235, -1.300294], // Default center (longitude, latitude)
    zoom: 10
});

// Add navigation controls (zoom and rotation)
map.addControl(new mapboxgl.NavigationControl());

let vehicleMarkers = new Map();
let origin = window.location.origin;
let websocketProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
let wsUrl = websocketProtocol + '//' + window.location.host + '/TMS/tracking';
let websocket = new WebSocket(wsUrl);

function createDriverMarkerEl() {
    const el = document.createElement('div');
    el.style.backgroundImage = 'url("' + markerImageUrl + '")';
    el.style.width = '40px';
    el.style.height = '40px';
    el.style.backgroundSize = 'cover';
    el.style.cursor = 'pointer';
    return el;
}

websocket.onmessage = function (evt) {
    const {driverName, lat, lng} = JSON.parse(evt.data);
    const coords = [lng, lat];

    let marker = vehicleMarkers.get(driverName);
    if (!marker) {
        // Create new marker + popup wiring
        const el = createDriverMarkerEl();
        const popup = new mapboxgl.Popup({offset: 25});
        let driverNam = vehicleMarkers.get(driverName);
        console.log(driverNam);
        // On click, fetch profile and populate your Bootstrap modal
        el.addEventListener('click', () => {
            popup.remove();  // hide any existing popup
            fetch(`/TMS/api/profile/driverDetails/${driverName}`)
                    .then(r => r.ok ? r.json() : Promise.reject(r.statusText))
                    .then(profile => {
                        // Populate your modal divs
                        document.getElementById('modal-username').textContent = profile.username;
                        document.getElementById('modal-email').textContent = profile.email;
                        document.getElementById('modal-phoneNumber').textContent = profile.phoneNumber || 'n/a';
                        
                    })
                    .catch(err => {
                        console.error('Profile fetch error:', err);
                        document.getElementById('modal-username').textContent = 'Error loading';
                    })
                    .finally(() => {
                        // Show the Bootstrap modal
                        const modalEl = document.getElementById('driverInfoModal');
                        new bootstrap.Modal(modalEl).show();
                    });
        });

        marker = new mapboxgl.Marker(el)
                .setLngLat(coords)
                .setPopup(popup)  // optional: you can still attach a lightweight popup
                .addTo(map);

        vehicleMarkers.set(driverName, marker);
    } else {
        // Move existing marker
        marker.setLngLat(coords);
    }

};


function updateVehiclePosition(vehicle) {
    const lngLat = [vehicle.lng, vehicle.lat];
    const point = turf.point(lngLat);


    if (!vehicleMarkers.has(vehicle.driverId)) {
        // Create a new marker
        const marker = new mapboxgl.Marker({
            color: 'red'
        }).setLngLat(lngLat).addTo(map);

        vehicleMarkers.set(vehicle.driverId, marker);
    } else {
        // Update existing marker position
        const marker = vehicleMarkers.get(vehicle.driverId);
        marker.setLngLat(lngLat);
    }

}

// Function to check if a vehicle is inside a geofence

// Function to send event to mapController
function callMapController(action, driverId) {
    fetch(`/api/mapController/${action}?driverId=${driverId}`, {
        method: 'POST'
    })
            .then(response => response.json())
            .then(data => console.log("Server response:", data))
            .catch(error => console.error("Error calling controller:", error));
}

// Reconnect WebSocket if disconnected
function reconnect() {
    setTimeout(() => {
        websocket = new WebSocket('ws://localhost:44711/Nganya/tracking');
        websocket.onopen = () => location.reload();
    }, 5000);
}
websocket.onclose = reconnect;