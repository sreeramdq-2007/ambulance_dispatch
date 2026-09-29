document.addEventListener('DOMContentLoaded', () => {
    // API Configuration
    const API_BASE = '/api';

    // State Variables
    let zonesMap = new Map();
    let ambulancesList = [];
    let callsList = [];

    // DOM Elements
    const locationZoneSelect = document.getElementById('locationZone');
    const ambulanceZoneSelect = document.getElementById('ambulanceZone');
    
    const emergencyCallForm = document.getElementById('emergency-call-form');
    const registerAmbulanceForm = document.getElementById('register-ambulance-form');
    
    const ambulancesTableBody = document.getElementById('ambulances-table-body');
    const callsTableBody = document.getElementById('calls-table-body');

    const filterAmbulanceStatus = document.getElementById('filter-ambulance-status');
    const btnRefresh = document.getElementById('btn-refresh');

    // Modal Elements
    const btnOpenCallModal = document.getElementById('btn-open-call-modal');
    const emergencyCallModal = document.getElementById('emergency-call-modal');
    const btnCloseCallModal = document.getElementById('btn-close-call-modal');
    const btnCancelCallModal = document.getElementById('btn-cancel-call-modal');
    const modalEmergencyCallForm = document.getElementById('modal-emergency-call-form');
    const modalCallerNameInput = document.getElementById('modalCallerName');
    const modalLocationZoneSelect = document.getElementById('modalLocationZone');
    const modalPrioritySelect = document.getElementById('modalPriority');

    // Stat Elements
    const statTotalAmbulances = document.getElementById('stat-total-ambulances');
    const statAvailableAmbulances = document.getElementById('stat-available-ambulances');
    const statBusyAmbulances = document.getElementById('stat-busy-ambulances');
    const statTotalCalls = document.getElementById('stat-total-calls');

    const callTableCount = document.getElementById('call-table-count');
    const ambulanceTableCount = document.getElementById('ambulance-table-count');

    // Result Banner Elements
    const assignmentBanner = document.getElementById('assignment-result-container');
    const resCallId = document.getElementById('res-call-id');
    const resCallerName = document.getElementById('res-caller-name');
    const resAmbulanceVehicle = document.getElementById('res-ambulance-vehicle');
    const resDistance = document.getElementById('res-distance');
    const resStatus = document.getElementById('res-status');

    // Initialize App
    init();

    async function init() {
        setupNavigation();
        setupEventListeners();
        await loadZones();
        await loadAllData();
    }

    function setupNavigation() {
        const navItems = document.querySelectorAll('.sidebar-nav .nav-item');
        const sections = {
            '#dashboard': document.getElementById('overview-section'),
            '#emergency': document.getElementById('emergency-section'),
            '#ambulances': document.getElementById('ambulance-section')
        };

        function navigateTo(hash, animate = true) {
            if (!hash || !sections[hash]) {
                hash = '#dashboard';
            }

            navItems.forEach(item => {
                if (item.getAttribute('href') === hash) {
                    item.classList.add('active');
                } else {
                    item.classList.remove('active');
                }
            });

            const target = sections[hash];
            if (target) {
                target.scrollIntoView({ behavior: animate ? 'smooth' : 'auto', block: 'start' });
                target.classList.remove('section-highlight');
                void target.offsetWidth; // force reflow
                target.classList.add('section-highlight');
                setTimeout(() => target.classList.remove('section-highlight'), 1600);
            }
        }

        navItems.forEach(item => {
            item.addEventListener('click', (e) => {
                e.preventDefault();
                const hash = item.getAttribute('href');
                history.pushState(null, null, hash);
                navigateTo(hash, true);
            });
        });

        // Clickable Stat Cards Navigation
        document.querySelectorAll('.stat-card.clickable').forEach(card => {
            card.addEventListener('click', () => {
                const targetId = card.getAttribute('data-target');
                if (targetId === 'emergency-section') {
                    history.pushState(null, null, '#emergency');
                    navigateTo('#emergency', true);
                } else if (targetId === 'ambulance-section') {
                    history.pushState(null, null, '#ambulances');
                    navigateTo('#ambulances', true);
                }
            });
        });

        window.addEventListener('hashchange', () => {
            navigateTo(window.location.hash, true);
        });

        if (window.location.hash) {
            setTimeout(() => navigateTo(window.location.hash, false), 150);
        }
    }

    function setupEventListeners() {
        btnRefresh.addEventListener('click', () => {
            loadAllData();
            showToast('Refreshed', 'Dashboard data synchronized with server.', 'success');
        });

        emergencyCallForm.addEventListener('submit', handleEmergencyCallSubmit);
        registerAmbulanceForm.addEventListener('submit', handleRegisterAmbulanceSubmit);

        filterAmbulanceStatus.addEventListener('change', renderAmbulanceTable);

        // Modal Event Listeners
        if (btnOpenCallModal) {
            btnOpenCallModal.addEventListener('click', openCallModal);
        }
        if (btnCloseCallModal) {
            btnCloseCallModal.addEventListener('click', closeCallModal);
        }
        if (btnCancelCallModal) {
            btnCancelCallModal.addEventListener('click', closeCallModal);
        }
        if (emergencyCallModal) {
            emergencyCallModal.addEventListener('click', (e) => {
                if (e.target === emergencyCallModal) closeCallModal();
            });
        }
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && emergencyCallModal && !emergencyCallModal.classList.contains('hidden')) {
                closeCallModal();
            }
        });
        if (modalEmergencyCallForm) {
            modalEmergencyCallForm.addEventListener('submit', handleModalEmergencyCallSubmit);
        }
    }

    function openCallModal() {
        if (emergencyCallModal) {
            emergencyCallModal.classList.remove('hidden');
            if (modalCallerNameInput) modalCallerNameInput.focus();
        }
    }

    function closeCallModal() {
        if (emergencyCallModal) {
            emergencyCallModal.classList.add('hidden');
            if (modalEmergencyCallForm) modalEmergencyCallForm.reset();
        }
    }

    // -------------------------------------------------------------
    // Data Loading Functions
    // -------------------------------------------------------------
    async function loadAllData() {
        await Promise.all([
            loadAmbulances(),
            loadEmergencyCalls()
        ]);
        updateDashboardStats();
    }

    async function loadZones() {
        try {
            const response = await fetch(`${API_BASE}/zones`);
            if (!response.ok) throw new Error('Failed to fetch zones');
            
            const zones = await response.json();
            zonesMap.clear();

            locationZoneSelect.innerHTML = '<option value="">Select Location Zone...</option>';
            ambulanceZoneSelect.innerHTML = '<option value="">Select Base Zone...</option>';
            if (modalLocationZoneSelect) {
                modalLocationZoneSelect.innerHTML = '<option value="">Select Location Zone...</option>';
            }

            zones.forEach(zone => {
                zonesMap.set(zone.id, zone);
                
                const opt1 = document.createElement('option');
                opt1.value = zone.id;
                opt1.textContent = `${zone.name} (${zone.location})`;
                locationZoneSelect.appendChild(opt1);

                const opt2 = document.createElement('option');
                opt2.value = zone.id;
                opt2.textContent = `${zone.name} (${zone.location})`;
                ambulanceZoneSelect.appendChild(opt2);

                if (modalLocationZoneSelect) {
                    const opt3 = document.createElement('option');
                    opt3.value = zone.id;
                    opt3.textContent = `${zone.name} (${zone.location})`;
                    modalLocationZoneSelect.appendChild(opt3);
                }
            });
        } catch (error) {
            console.error(error);
            showToast('Error', 'Failed to load zones from server.', 'error');
        }
    }

    async function loadAmbulances() {
        try {
            const response = await fetch(`${API_BASE}/ambulances`);
            if (!response.ok) throw new Error('Failed to fetch ambulances');
            ambulancesList = await response.json();
            renderAmbulanceTable();
        } catch (error) {
            console.error(error);
            showToast('Error', 'Failed to load ambulance fleet.', 'error');
        }
    }

    async function loadEmergencyCalls() {
        try {
            const response = await fetch(`${API_BASE}/emergency-calls`);
            if (!response.ok) throw new Error('Failed to fetch emergency calls');
            callsList = await response.json();
            renderEmergencyCallsTable();
        } catch (error) {
            console.error(error);
            showToast('Error', 'Failed to load emergency calls.', 'error');
        }
    }

    // -------------------------------------------------------------
    // Rendering Functions
    // -------------------------------------------------------------
    function updateDashboardStats() {
        const totalAmbulances = ambulancesList.length;
        const availableAmbulances = ambulancesList.filter(a => a.status === 'AVAILABLE').length;
        const busyAmbulances = ambulancesList.filter(a => a.status === 'BUSY').length;
        const totalCalls = callsList.length;

        statTotalAmbulances.textContent = totalAmbulances;
        statAvailableAmbulances.textContent = availableAmbulances;
        statBusyAmbulances.textContent = busyAmbulances;
        statTotalCalls.textContent = totalCalls;

        ambulanceTableCount.textContent = `${totalAmbulances} units`;
        callTableCount.textContent = `${totalCalls} calls`;
    }

    function renderAmbulanceTable() {
        const filter = filterAmbulanceStatus.value;
        const filtered = filter === 'ALL' ? ambulancesList : ambulancesList.filter(a => a.status === filter);

        if (filtered.length === 0) {
            ambulancesTableBody.innerHTML = `<tr><td colspan="6" class="empty-state">No ambulances found for status filter.</td></tr>`;
            return;
        }

        ambulancesTableBody.innerHTML = filtered.map(a => `
            <tr>
                <td class="font-bold">#${a.id}</td>
                <td class="highlight-vehicle">${escapeHtml(a.vehicleNumber)}</td>
                <td>${a.currentZone ? escapeHtml(a.currentZone.name) : 'Unassigned'}</td>
                <td><span class="badge badge-${a.status.toLowerCase()}">${a.status}</span></td>
                <td>${formatDate(a.lastUpdated)}</td>
                <td class="text-right">
                    <select class="form-control form-control-sm" onchange="window.updateAmbulanceStatus(${a.id}, this.value)" style="display:inline-block; width: auto;">
                        <option value="AVAILABLE" ${a.status === 'AVAILABLE' ? 'selected' : ''}>AVAILABLE</option>
                        <option value="BUSY" ${a.status === 'BUSY' ? 'selected' : ''}>BUSY</option>
                        <option value="MAINTENANCE" ${a.status === 'MAINTENANCE' ? 'selected' : ''}>MAINTENANCE</option>
                        <option value="OUT_OF_SERVICE" ${a.status === 'OUT_OF_SERVICE' ? 'selected' : ''}>OUT_OF_SERVICE</option>
                    </select>
                </td>
            </tr>
        `).join('');
    }

    function renderEmergencyCallsTable() {
        if (callsList.length === 0) {
            callsTableBody.innerHTML = `<tr><td colspan="9" class="empty-state">No emergency calls recorded yet.</td></tr>`;
            return;
        }

        // Sort descending by ID
        const sorted = [...callsList].sort((a, b) => b.id - a.id);

        callsTableBody.innerHTML = sorted.map(c => {
            const assignedVehicle = c.assignedAmbulance ? c.assignedAmbulance.vehicleNumber : 'Not Assigned';
            const distanceText = c.calculatedDistanceKm ? `${c.calculatedDistanceKm} km` : '--';
            
            let actionButtons = '';
            if (c.status === 'PENDING') {
                actionButtons = `
                    <button class="btn btn-primary btn-sm" onclick="window.assignAmbulanceToCall(${c.id})">
                        Assign Nearest
                    </button>
                    <button class="btn btn-secondary btn-sm" onclick="window.updateCallStatus(${c.id}, 'CANCELLED')">
                        Cancel
                    </button>
                `;
            } else if (c.status === 'ASSIGNED') {
                actionButtons = `
                    <button class="btn btn-primary btn-sm" onclick="window.updateCallStatus(${c.id}, 'COMPLETED')">
                        Mark Completed
                    </button>
                `;
            } else {
                actionButtons = `<span class="field-title">No Action</span>`;
            }

            return `
                <tr>
                    <td class="font-bold">#${c.id}</td>
                    <td>${escapeHtml(c.callerName)}</td>
                    <td>${c.locationZone ? escapeHtml(c.locationZone.name) : 'N/A'}</td>
                    <td><span class="badge badge-priority-${c.priority.toLowerCase()}">${c.priority}</span></td>
                    <td><span class="badge badge-${c.status.toLowerCase()}">${c.status}</span></td>
                    <td class="${c.assignedAmbulance ? 'highlight-vehicle' : ''}">${escapeHtml(assignedVehicle)}</td>
                    <td><span class="distance-pill">${distanceText}</span></td>
                    <td>${formatDate(c.createdAt)}</td>
                    <td class="text-right">${actionButtons}</td>
                </tr>
            `;
        }).join('');
    }

    // -------------------------------------------------------------
    // Action Handlers
    // -------------------------------------------------------------
    async function handleEmergencyCallSubmit(e) {
        e.preventDefault();
        
        const callerName = document.getElementById('callerName').value.trim();
        const locationZoneId = document.getElementById('locationZone').value;
        const priority = document.getElementById('priority').value;

        if (!callerName || !locationZoneId) {
            showToast('Validation Error', 'Please complete all required fields.', 'warning');
            return;
        }

        const payload = {
            callerName: callerName,
            locationZoneId: parseInt(locationZoneId),
            priority: priority,
            autoAssign: true
        };

        try {
            const response = await fetch(`${API_BASE}/emergency-calls`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                const errData = await response.json();
                throw new Error(errData.message || 'Failed to create emergency call');
            }

            const callData = await response.json();
            
            showToast('Success', `Emergency call #${callData.id} created!`, 'success');
            
            if (callData.assignedAmbulance) {
                displayAssignmentBanner({
                    emergencyCallId: callData.id,
                    callerName: callData.callerName,
                    assignedAmbulanceVehicleNumber: callData.assignedAmbulance.vehicleNumber,
                    distanceKm: callData.calculatedDistanceKm || 5.0,
                    status: callData.status
                });
            } else {
                showToast('Notice', 'Call created as PENDING. No AVAILABLE ambulance found at this moment.', 'warning');
            }

            emergencyCallForm.reset();
            await loadAllData();
        } catch (error) {
            console.error(error);
            showToast('Assignment Failed', error.message, 'error');
        }
    }

    async function handleModalEmergencyCallSubmit(e) {
        e.preventDefault();

        const callerName = modalCallerNameInput.value.trim();
        const locationZoneId = modalLocationZoneSelect.value;
        const priority = modalPrioritySelect.value;

        if (!callerName || !locationZoneId) {
            showToast('Validation Error', 'Please complete all required fields.', 'warning');
            return;
        }

        const payload = {
            callerName: callerName,
            locationZoneId: parseInt(locationZoneId),
            priority: priority,
            autoAssign: true
        };

        try {
            const response = await fetch(`${API_BASE}/emergency-calls`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                const errData = await response.json();
                throw new Error(errData.message || 'Failed to create emergency call');
            }

            const callData = await response.json();
            
            showToast('Success', `Emergency call #${callData.id} created!`, 'success');
            
            if (callData.assignedAmbulance) {
                displayAssignmentBanner({
                    emergencyCallId: callData.id,
                    callerName: callData.callerName,
                    assignedAmbulanceVehicleNumber: callData.assignedAmbulance.vehicleNumber,
                    distanceKm: callData.calculatedDistanceKm || 5.0,
                    status: callData.status
                });
            } else {
                showToast('Notice', 'Call created as PENDING. No AVAILABLE ambulance found at this moment.', 'warning');
            }

            closeCallModal();
            await loadAllData();
        } catch (error) {
            console.error(error);
            showToast('Assignment Failed', error.message, 'error');
        }
    }

    async function handleRegisterAmbulanceSubmit(e) {
        e.preventDefault();

        const vehicleNumber = document.getElementById('vehicleNumber').value.trim();
        const zoneId = document.getElementById('ambulanceZone').value;
        const status = document.getElementById('ambulanceStatus').value;

        if (!vehicleNumber || !zoneId) {
            showToast('Validation Error', 'Please provide vehicle number and zone.', 'warning');
            return;
        }

        const payload = {
            vehicleNumber: vehicleNumber,
            zoneId: parseInt(zoneId),
            status: status
        };

        try {
            const response = await fetch(`${API_BASE}/ambulances`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                const errData = await response.json();
                throw new Error(errData.message || 'Failed to register ambulance');
            }

            const amb = await response.json();
            showToast('Ambulance Registered', `Vehicle ${amb.vehicleNumber} registered successfully.`, 'success');
            registerAmbulanceForm.reset();
            await loadAllData();
        } catch (error) {
            console.error(error);
            showToast('Registration Error', error.message, 'error');
        }
    }

    // Global Functions Attached to Window for Inline Event Handlers
    window.assignAmbulanceToCall = async function(callId) {
        try {
            const response = await fetch(`${API_BASE}/emergency-calls/${callId}/assign`, {
                method: 'POST'
            });

            if (!response.ok) {
                const err = await response.json();
                throw new Error(err.message || 'Failed to assign ambulance');
            }

            const result = await response.json();
            showToast('Ambulance Assigned', result.message, 'success');
            displayAssignmentBanner(result);
            await loadAllData();
        } catch (error) {
            console.error(error);
            showToast('Assignment Error', error.message, 'error');
        }
    };

    window.updateAmbulanceStatus = async function(id, newStatus) {
        try {
            const response = await fetch(`${API_BASE}/ambulances/${id}/status`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ status: newStatus })
            });

            if (!response.ok) {
                const err = await response.json();
                throw new Error(err.message || 'Failed to update status');
            }

            showToast('Status Updated', `Ambulance #${id} status changed to ${newStatus}.`, 'success');
            await loadAllData();
        } catch (error) {
            console.error(error);
            showToast('Update Failed', error.message, 'error');
            await loadAmbulances(); // revert select UI
        }
    };

    window.updateCallStatus = async function(id, newStatus) {
        try {
            const response = await fetch(`${API_BASE}/emergency-calls/${id}/status`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ status: newStatus })
            });

            if (!response.ok) {
                const err = await response.json();
                throw new Error(err.message || 'Failed to update call status');
            }

            showToast('Call Updated', `Emergency Call #${id} marked as ${newStatus}.`, 'success');
            await loadAllData();
        } catch (error) {
            console.error(error);
            showToast('Update Error', error.message, 'error');
        }
    };

    function displayAssignmentBanner(res) {
        resCallId.textContent = `#${res.emergencyCallId}`;
        resCallerName.textContent = res.callerName || 'N/A';
        resAmbulanceVehicle.textContent = res.assignedAmbulanceVehicleNumber || 'TN-01-XXXX';
        resDistance.textContent = `${res.distanceKm} km`;
        resStatus.textContent = res.status || 'ASSIGNED';

        assignmentBanner.classList.remove('hidden');
        assignmentBanner.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }

    // -------------------------------------------------------------
    // Utility & Toast Functions
    // -------------------------------------------------------------
    function showToast(title, message, type = 'info') {
        const container = document.getElementById('toast-container');
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        
        toast.innerHTML = `
            <div class="toast-body">
                <h4>${escapeHtml(title)}</h4>
                <p>${escapeHtml(message)}</p>
            </div>
        `;

        container.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateX(20px)';
            toast.style.transition = 'all 0.3s ease';
            setTimeout(() => toast.remove(), 300);
        }, 4000);
    }

    function formatDate(dateString) {
        if (!dateString) return 'N/A';
        const d = new Date(dateString);
        return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) + ' ' + d.toLocaleDateString();
    }

    function escapeHtml(text) {
        if (!text) return '';
        return text
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }
});
