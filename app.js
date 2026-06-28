document.addEventListener('DOMContentLoaded', () => {
    const grid = document.getElementById('modulesGrid');
    const searchInput = document.getElementById('searchInput');
    const filterChips = document.querySelectorAll('.chip');
    const copyBtn = document.getElementById('copyBtn');
    const repoUrlInput = document.getElementById('repoUrl');
    const countAll = document.getElementById('count-all');
    const statTotal = document.getElementById('stat-total');
    const toast = document.getElementById('toast');

    let allModules = [];
    let currentFilter = 'all';
    let debounceTimer;

    // Show toast message
    function showToast(message) {
        if (!toast) return;
        toast.textContent = message;
        toast.classList.add('show');
        setTimeout(() => {
            toast.classList.remove('show');
        }, 2500);
    }

    // Fetch plugins.json
    fetch('plugins.json')
        .then(response => {
            if (!response.ok) throw new Error('Network error');
            return response.json();
        })
        .then(data => {
            allModules = data;
            if (countAll) countAll.textContent = allModules.length;
            if (statTotal) statTotal.textContent = allModules.length;
            renderModules(allModules);
        })
        .catch(err => {
            console.error('Plugin manifest yüklenemedi:', err);
            grid.innerHTML = '<p class="error-msg">Modül kataloğu yüklenirken bir hata oluştu.</p>';
        });

    // High performance DOM rendering using DocumentFragment
    function renderModules(modules) {
        if (modules.length === 0) {
            grid.innerHTML = '<div class="no-results" style="grid-column: 1/-1; text-align: center; padding: 60px; color: var(--text-muted);"><p style="font-size: 1.2rem;">🔍 Aradığınız kriterlere uygun modül bulunamadı.</p></div>';
            return;
        }

        const fragment = document.createDocumentFragment();
        const defaultIcon = 'https://t2.gstatic.com/faviconV2?client=SOCIAL&type=FAVICON&fallback_opts=TYPE,SIZE,URL&url=https://github.com&size=64';

        modules.forEach(mod => {
            const card = document.createElement('div');
            card.className = 'card';
            
            const isMaintenance = mod.status !== 1;
            const statusClass = isMaintenance ? 'status-maintenance' : 'status-active';
            const statusText = isMaintenance ? 'Bakımda' : 'Çevrimiçi';
            const iconUrl = mod.iconUrl || defaultIcon;

            const tagsHtml = (mod.tvTypes || []).map(t => `<span class="tag">${t}</span>`).join('');

            card.innerHTML = `
                <div>
                    <div class="card-top">
                        <img src="${iconUrl}" alt="${mod.name}" class="module-icon" loading="lazy" width="52" height="52" onerror="this.src='${defaultIcon}'">
                        <div>
                            <h4 class="module-title">${mod.name}</h4>
                            <span class="status-badge ${statusClass}">${statusText}</span>
                        </div>
                    </div>
                    <p class="card-desc">${mod.description || 'Türkçe içerik modülü.'}</p>
                    <div class="tags-row">${tagsHtml}</div>
                </div>
                <div class="card-actions">
                    <a href="${mod.url}" download class="btn btn-secondary glass-btn">.CS3 İndir</a>
                </div>
            `;
            fragment.appendChild(card);
        });

        grid.innerHTML = '';
        grid.appendChild(fragment);
    }

    // Debounced Search and Filter logic
    function filterModules() {
        const query = searchInput.value.toLowerCase().trim();

        const filtered = allModules.filter(mod => {
            const matchesSearch = !query || 
                                mod.name.toLowerCase().includes(query) ||
                                (mod.description && mod.description.toLowerCase().includes(query)) ||
                                (mod.tvTypes && mod.tvTypes.some(t => t.toLowerCase().includes(query)));

            if (!matchesSearch) return false;
            if (currentFilter === 'all') return true;
            if (!mod.tvTypes) return false;

            const types = mod.tvTypes.map(t => t.toLowerCase());

            if (currentFilter === 'movie') return types.includes('movie') || types.includes('animemovie');
            if (currentFilter === 'tvseries') return types.includes('tvseries');
            if (currentFilter === 'anime') return types.includes('anime') || types.includes('animemovie') || types.includes('ova');
            if (currentFilter === 'asiandrama') return types.includes('asiandrama');
            if (currentFilter === 'live') return types.includes('live');
            if (currentFilter === 'nsfw') return types.includes('nsfw');

            return true;
        });

        renderModules(filtered);
    }

    searchInput.addEventListener('input', () => {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(filterModules, 120);
    });

    filterChips.forEach(chip => {
        chip.addEventListener('click', () => {
            filterChips.forEach(c => c.classList.remove('active'));
            chip.classList.add('active');
            currentFilter = chip.getAttribute('data-filter');
            filterModules();
        });
    });

    // Copy Button with Toast
    if (copyBtn && repoUrlInput) {
        copyBtn.addEventListener('click', () => {
            repoUrlInput.select();
            navigator.clipboard.writeText(repoUrlInput.value).then(() => {
                showToast('🚀 Repo bağlantısı kopyalandı!');
            });
        });
    }
});
