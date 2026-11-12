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

(function () {
	var THEME_KEY = 'crdr-theme';
	var toggleBtn = document.getElementById('crdrThemeToggle');

	function getTheme() {
		return document.documentElement.getAttribute('data-theme') || 'light';
	}

	function applyTheme(theme) {
		document.documentElement.setAttribute('data-theme', theme);
		localStorage.setItem(THEME_KEY, theme);
		if (toggleBtn) {
			var icon = toggleBtn.querySelector('i');
			if (icon) {
				icon.className = theme === 'dark' ? 'fa-solid fa-sun' : 'fa-solid fa-moon';
			}
			toggleBtn.setAttribute('title', theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode');
		}
	}

	applyTheme(getTheme());

	if (toggleBtn) {
		toggleBtn.addEventListener('click', function () {
			applyTheme(getTheme() === 'dark' ? 'light' : 'dark');
		});
	}

	window.crdrShareWhatsApp = async function (opts) {
		var title = (opts && opts.title) || document.title || 'CrDr Financial Report';
		var summary = (opts && opts.summary) || (title + ' generated via CrDr Report (Extenzia Technology Solutions)');
		var pdfUrl = opts && opts.pdfUrl;
		var fileName = (opts && opts.fileName) || 'report.pdf';

		if (pdfUrl && navigator.share && navigator.canShare) {
			try {
				var resp = await fetch(pdfUrl, { credentials: 'same-origin' });
				if (resp.ok) {
					var blob = await resp.blob();
					var file = new File([blob], fileName, { type: 'application/pdf' });
					if (navigator.canShare({ files: [file] })) {
						await navigator.share({
							title: title,
							text: summary,
							files: [file]
						});
						return;
					}
				}
			} catch (e) {
				if (e && e.name === 'AbortError') {
					return;
				}
			}
		}

		var shareText = summary + (pdfUrl ? ('\nDownload PDF: ' + new URL(pdfUrl, window.location.origin).href) : '');
		window.open('https://wa.me/?text=' + encodeURIComponent(shareText), '_blank', 'noopener');
	};

	document.querySelectorAll('.js-crdr-wa-share').forEach(function (btn) {
		btn.addEventListener('click', function () {
			var title = btn.dataset.title || 'CrDr Report';
			var company = btn.dataset.company || '';
			var period = btn.dataset.period || '';
			window.crdrShareWhatsApp({
				title: title + (company ? ' - ' + company : ''),
				summary: title + (company ? ' for ' + company : '') + (period ? ' (' + period + ')' : ''),
				pdfUrl: btn.dataset.pdfUrl,
				fileName: btn.dataset.fileName || 'report.pdf'
			});
		});
	});
})();
