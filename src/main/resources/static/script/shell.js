(function () {
	var STORAGE_KEY = 'crdrSidebarCollapsed';
	var sidebar = document.getElementById('crdrSidebar');
	if (!sidebar) {
		return;
	}

	document.body.classList.add('crdr-has-sidebar');
	var backdrop = document.getElementById('crdrSidebarBackdrop');
	var toggleBtn = document.getElementById('crdrSidebarToggle');

	function isDesktop() {
		return window.innerWidth >= 992;
	}

	if (isDesktop() && localStorage.getItem(STORAGE_KEY) === 'true') {
		document.body.classList.add('crdr-sidebar-collapsed');
	}

	if (toggleBtn) {
		toggleBtn.addEventListener('click', function () {
			if (isDesktop()) {
				document.body.classList.toggle('crdr-sidebar-collapsed');
				localStorage.setItem(STORAGE_KEY,
					document.body.classList.contains('crdr-sidebar-collapsed') ? 'true' : 'false');
			} else {
				sidebar.classList.toggle('is-open');
				if (backdrop) {
					backdrop.classList.toggle('is-open');
				}
			}
		});
	}

	if (backdrop) {
		backdrop.addEventListener('click', function () {
			sidebar.classList.remove('is-open');
			backdrop.classList.remove('is-open');
		});
	}

	var path = window.location.pathname;
	var links = sidebar.querySelectorAll('.crdr-sidebar-link');
	links.forEach(function (link) {
		var href = link.getAttribute('href');
		if (href && (path === href || (href !== '/' && path.indexOf(href) === 0 && href !== '/reports'))) {
			link.classList.add('is-active');
		}
	});
})();
