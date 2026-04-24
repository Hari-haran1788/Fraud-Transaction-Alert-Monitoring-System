let balance = 50000;
let html5QrCode;

window.onload = () => {
    const savedUser = sessionStorage.getItem('activeUser');
    if (savedUser) {
        document.getElementById('landing-page').style.display = 'none';
        document.getElementById('dashboard').style.display = 'block';
        document.getElementById('u-dash-name').innerText = savedUser;

        const loginInput = document.getElementById('l-user');
        if (loginInput) loginInput.value = savedUser;

        refreshBalance(savedUser);
        loadTransactionHistory(savedUser);
    }
};

function refreshBalance(username) {
    fetch("http://localhost:8080/api/customer_registration")
        .then(res => res.json())
        .then(customers => {
            const user = customers.find(c =>
                c.user_name && username &&
                c.user_name.toLowerCase() === username.toLowerCase()
            );
            if (user) {
                balance = user.balance;
                const balElement = document.getElementById('d-balance');
                if (balElement) balElement.innerText = `₹ ${balance.toLocaleString()}`;
                const accNoElement = document.querySelector('.bal-gradient-card .acc-num');
                if (accNoElement) {
                    const realAccNo = user.accountNumber || user.account_number || user.accountnumber;
                    accNoElement.innerText = realAccNo ? `A/c No: ${realAccNo}` : `A/c No: Not Assigned`;
                }
            }
        })
        .catch(err => console.error("Balance fetch error:", err));
}

function loadTransactionHistory(username) {
    const list = document.getElementById('txn-list');
    if (!list) return;
    list.innerHTML = '';
    fetch(`http://localhost:8080/api/transactions/${username}`)
        .then(res => res.json())
        .then(transactions => {
            if (!transactions || transactions.length === 0) {
                list.innerHTML = '<div class="txn-row"><span>No recent activity</span></div>';
                return;
            }
            transactions.forEach(txn => {
                const type = txn.type === 'CREDIT' ? 'plus' : 'minus';
                updateHistory(txn.description, txn.amount, type);
            });
        })
        .catch(err => console.error("History fetch error:", err));
}

function updateHistory(label, amount, type) {
    const list = document.getElementById('txn-list');
    if (!list) return;
    const colorClass = type === 'plus' ? 'txt-green' : 'txt-red';
    const symbol = type === 'plus' ? '+' : '-';
    const newTxn = `<div class="txn-row"><span>${label}</span><span class="${colorClass}">${symbol} ₹${amount.toLocaleString()}</span></div>`;
    list.insertAdjacentHTML('afterbegin', newTxn);
}

function register() {
    const user = {
        name: document.getElementById('r-name').value.trim(),
        user_name: document.getElementById('r-user').value.trim().toLowerCase(),
        recoveryEmail: document.getElementById('r-recovery').value.trim(),
        password: document.getElementById('r-pass').value,
        pin: parseInt(document.getElementById('r-pin').value),
        ph_no: parseInt(document.getElementById('r-phone').value)
    };
    if (!user.name || !user.user_name || !user.recoveryEmail) { alert("❌ Fill all fields!"); return; }

    fetch("http://localhost:8080/api/customer_registration", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(user)
    })
        .then(async response => {
            const data = await response.text();
            if (!response.ok) throw new Error(data);
            alert("✅ Registration Successful!!!");
            openAuth('login');
        })
        .catch(error => alert("❌ Registration Failed: " + error.message));
}

function login() {
    const username = document.getElementById('l-user').value;
    const password = document.getElementById('l-pass').value;

    fetch("http://localhost:8080/api/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
    })
        .then(res => res.json())
        .then(data => {
            if (data.status === "success") {
                alert("✅ Login Successful!!!");
                sessionStorage.setItem('activeUser', username);
                location.reload();
            } else if (data.status === "blocked") {
                alert("❌ " + data.message);
                sessionStorage.removeItem('activeUser');
                location.reload(); // Force redirect to Landing Page
            } else {
                alert("❌ " + data.message);
            }
        })
        .catch(err => alert("❌ Failed to login. Is backend running?"));
}

function openAction(type) {
    resetAllForms();
    const modal = document.getElementById('actionModal');
    const body = document.getElementById('a-body');
    const btn = document.getElementById('a-btn');
    const username = sessionStorage.getItem('activeUser');

    modal.style.display = 'flex';
    body.innerHTML = '';

    if (type === 'transfer') {
        document.getElementById('a-title').innerText = "Transfer Funds";
        body.innerHTML = `
            <input type="text" id="t-acc" placeholder="Recipient">
            <input type="number" id="f-amt" placeholder="Amount">
            <input type="password" id="f-pin" placeholder="PIN" autocomplete="one-time-code">`;

        btn.onclick = () => {
            fetch("http://localhost:8080/api/transfer", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ userName: username, toUser: document.getElementById('t-acc').value, amount: document.getElementById('f-amt').value, pin: document.getElementById('f-pin').value })
            }).then(async res => {
                const data = await res.text();
                if (!res.ok) {
                    alert(data);
                    if (data.toUpperCase().includes("BLOCKED")) {
                        sessionStorage.removeItem('activeUser');
                        location.reload(); // Redirect to Landing Page
                    }
                    return;
                }
                alert("✅ Transferred Successfully!!!");
                location.reload();
            });
        };
    } else if (type === 'recharge') {
        document.getElementById('a-title').innerText = "Mobile Recharge";
        body.innerHTML = `
            <input type="number" id="r-mob" placeholder="Mobile Number">
            <select id="r-op"><option>Jio</option><option>Airtel</option><option>VI</option></select>
            <input type="number" id="f-amt" placeholder="Amount">
            <input type="password" id="f-pin" placeholder="PIN" autocomplete="one-time-code">`;

        btn.onclick = () => {
            fetch("http://localhost:8080/api/recharge", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username: username, mobile: document.getElementById('r-mob').value, amount: document.getElementById('f-amt').value, pin: document.getElementById('f-pin').value })
            }).then(async res => {
                const data = await res.text();
                if (!res.ok) {
                    alert(data);
                    if (data.toUpperCase().includes("BLOCKED")) {
                        sessionStorage.removeItem('activeUser');
                        location.reload(); // Redirect to Landing Page
                    }
                    return;
                }
                alert("✅ Recharge Successful!!!");
                location.reload();
            });
        };
    } else if (type === 'pin') {
        document.getElementById('a-title').innerText = "Change Security PIN";
        body.innerHTML = `
            <input type="password" id="old-pin" placeholder="Old PIN" autocomplete="off">
            <input type="password" id="new-pin" placeholder="New PIN" autocomplete="new-password">
            <input type="password" id="conf-pin" placeholder="Confirm New PIN" autocomplete="new-password">`;

        btn.onclick = () => {
            const oldPin = document.getElementById('old-pin').value;
            const newPin = document.getElementById('new-pin').value;
            const confirmPin = document.getElementById('conf-pin').value;
            fetch("http://localhost:8080/api/changePin", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username, oldPin, newPin, confirmPin })
            }).then(async res => {
                const msg = await res.text();
                alert(msg);
                if (res.ok) location.reload();
            });
        };
    } else if (type === 'mob') {
        document.getElementById('a-title').innerText = "Update Mobile Number";
        body.innerHTML = `
            <input type="number" id="old-mob" placeholder="Old Mobile">
            <input type="number" id="new-mob" placeholder="New Mobile">
            <input type="password" id="f-pin" placeholder="PIN" autocomplete="one-time-code">`;

        btn.onclick = () => {
            const oldMobile = document.getElementById('old-mob').value;
            const newMobile = document.getElementById('new-mob').value;
            const pin = document.getElementById('f-pin').value;
            fetch("http://localhost:8080/api/updateMobile", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ username: username, oldMobile: parseInt(oldMobile), newMobile: parseInt(newMobile), pin: parseInt(pin) })
            }).then(async res => {
                const msg = await res.text();
                alert(msg);
                if (res.ok) location.reload();
            });
        };
    }
}

function openAuth(m) {
    resetAllForms();
    document.getElementById('authModal').style.display = 'flex';
    document.getElementById('login-fields').style.display = m === 'login' ? 'block' : 'none';
    document.getElementById('reg-fields').style.display = m === 'reg' ? 'block' : 'none';
}

function closeM() {
    stopScanner();
    document.querySelectorAll('.modal').forEach(m => m.style.display = 'none');
}

function toggleBal() { document.getElementById('d-balance').classList.toggle('show'); }

function downloadPDF() {
    const username = sessionStorage.getItem('activeUser');
    if (!username) { alert("❌ Log in first"); return; }
    window.location.href = `http://localhost:8080/api/download-statement/${username}`;
}

function resetAllForms() {
    const modalInputs = document.querySelectorAll('.modal input');
    modalInputs.forEach(input => {
        input.value = "";
        input.style.backgroundColor = "";
    });
}

function handleLogout() {
    console.log("Logging out...");

    sessionStorage.removeItem('activeUser');
    sessionStorage.clear();

    window.location.href = window.location.pathname;
}

function openSupport() {
    closeM();
    document.getElementById('support-modal').style.display = 'flex';
    backToStep1();
}

function closeSupport() { document.getElementById('support-modal').style.display = 'none'; }

function backToStep1() {
    document.getElementById('support-step-1').style.display = 'block';
    document.getElementById('support-step-2').style.display = 'none';
}

function sendOtp() {
    const username = document.getElementById('s-user').value.trim();
    const recoveryEmail = document.getElementById('s-recovery').value.trim();
    fetch("http://localhost:8080/api/request-otp", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, recoveryEmail })
    }).then(async res => {
        const msg = await res.text();
        if (res.ok) {
            alert(msg);
            document.getElementById('support-step-1').style.display = 'none';
            document.getElementById('support-step-2').style.display = 'block';
        } else { alert("❌ " + msg); }
    });
}

function verifyOtp() {
    const username = document.getElementById('s-user').value.trim();
    const otp = document.getElementById('s-otp').value.trim();
    fetch(`http://localhost:8080/api/verify-otp?username=${username}&otp=${otp}`, { method: "POST" })
        .then(async res => {
            const msg = await res.text();
            if (res.ok) {
                alert(msg);
                closeSupport();
                openAuth('login');
            } else { alert(" " + msg); }
        });
}

function showMyQR() {
    const username = sessionStorage.getItem('activeUser');
    const qrContainer = document.getElementById("qrcode");
    qrContainer.innerHTML = "";
    document.getElementById('qr-modal').style.display = 'flex';
    document.getElementById('qr-label').innerText = `Pay to: ${username}`;
    new QRCode(qrContainer, {
        text: `strapay://payment?recipient=${username}`,
        width: 200,
        height: 200,
        colorDark: "#1e3c72",
        colorLight: "#ffffff",
        correctLevel: QRCode.CorrectLevel.H
    });
}

function startScanner() {
    document.getElementById('scanner-modal').style.display = 'flex';
    if (!html5QrCode) { html5QrCode = new Html5Qrcode("reader"); }
    html5QrCode.start(
        { facingMode: "environment" },
        { fps: 10, qrbox: { width: 250, height: 250 } },
        (decodedText) => {
            if (decodedText.startsWith("strapay://payment")) {
                stopScanner();
                handleQRPayment(decodedText);
            }
        }
    ).catch(err => {
        alert("Camera access denied.");
        document.getElementById('scanner-modal').style.display = 'none';
    });
}

function stopScanner() {
    if (html5QrCode && html5QrCode.isScanning) {
        html5QrCode.stop().then(() => {
            document.getElementById('scanner-modal').style.display = 'none';
        });
    } else { document.getElementById('scanner-modal').style.display = 'none'; }
}

function handleQRPayment(url) {
    const params = new URLSearchParams(url.split('?')[1]);
    const recipient = params.get('recipient');
    openAction('transfer');
    setTimeout(() => {
        const inputField = document.getElementById('t-acc');
        if (inputField) {
            inputField.value = recipient;
            inputField.style.backgroundColor = "#e8f5e9";
        }
    }, 500);
}

window.onclick = (e) => {
    if (e.target.classList.contains('modal')) closeM();
    if (e.target === document.getElementById('support-modal')) closeSupport();
}