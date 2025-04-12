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

// Store vehicle markers

let vehicleMarkers = new Map();

let origin = window.location.origin;

// Replace "http" or "https" with "ws" or "wss"
let websocketProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';

// Construct the WebSocket URL
let wsUrl = websocketProtocol + '//' + window.location.host + '/TMS/tracking';
let websocket = new WebSocket(wsUrl);

websocket.onmessage = function (event) {
    const data = JSON.parse(event.data);
    updateVehiclePosition(data);
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