const API_URL = 'https://api-amigo-secreto.onrender.com/api';
let todosEventos = [];
let eventoSelecionado = null;

document.addEventListener("DOMContentLoaded", () => {
    checkSession();
    document.getElementById('formEvento')?.addEventListener('submit', criarEvento);
    document.getElementById('formJoin')?.addEventListener('submit', entrarEmEvento);
});

function openModal(id) { document.getElementById(id).classList.remove('hidden'); }
function closeModal(id) { document.getElementById(id).classList.add('hidden'); }
function showScreen(screenId) {
    document.querySelectorAll('.screen-wrapper').forEach(el => el.classList.add('hidden'));
    document.getElementById(screenId).classList.remove('hidden');
}

function fazerLoginGoogle() { window.location.href = "https://api-amigo-secreto.onrender.com/oauth2/authorization/google"; }
async function logout() {
    try { await fetch(`${API_URL.replace('/api', '')}/logout`, { method: 'POST', credentials: 'include' }); } 
    catch(e) {} finally { showScreen('screen-login'); }
}

async function checkSession() {
    try {
        const resMeus = await fetch(`${API_URL}/eventos/meus-eventos`, { credentials: 'include' });
        if (resMeus.ok) {
            const meusEventos = await resMeus.json();
            const resPart = await fetch(`${API_URL}/eventos/participando`, { credentials: 'include' });
            const participando = resPart.ok ? await resPart.json() : [];

            todosEventos = [
                ...meusEventos.map(e => ({...e, tipo: 'organizador'})),
                ...participando.map(e => ({...e, tipo: 'participante'}))
            ];
            filtrarLista('todos');
            showScreen('screen-dashboard');
        } else { showScreen('screen-login'); }
    } catch (error) { showScreen('screen-login'); }
}

function filtrarLista(filtro) {
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
        if(btn.innerText.toLowerCase() === filtro || (filtro === 'todos' && btn.innerText === 'Todos')) btn.classList.add('active');
    });
    let lista = (filtro === 'todos') ? todosEventos : todosEventos.filter(ev => ev.tipo === filtro);
    renderizarListaUnificada(lista);
}

function renderizarListaUnificada(lista) {
    const container = document.getElementById('listaEventosUnified');
    container.innerHTML = '';
    if (lista.length === 0) {
        container.innerHTML = '<p class="empty-msg">Nenhum evento encontrado.</p>';
        return;
    }
    lista.forEach(ev => {
        const div = document.createElement('div');
        div.className = 'event-item';
        div.onclick = () => abrirDetalhes(ev);
        
        const badgeClass = ev.tipo === 'organizador' ? 'badge-org' : 'badge-part';
        const badgeText = ev.tipo === 'organizador' ? 'Organizador' : 'Participante';
        div.innerHTML = `
            <div class="event-info">
                <h4>${ev.nomeEvento}</h4>
                <span>Cód: ${ev.codigoConvite}</span>
            </div>
            <div class="badge ${badgeClass}">${badgeText}</div>
        `;
        container.appendChild(div);
    });
}

function abrirDetalhes(ev) {
    eventoSelecionado = ev;
    
    document.getElementById('detalheTitulo').innerText = ev.nomeEvento;
    document.getElementById('detalheCodigo').innerText = ev.codigoConvite;
    
    const areaPart = document.getElementById('areaParticipantes');
    const listaEmails = document.getElementById('listaEmails');
    const btnSortear = document.getElementById('btnSortear');
    const btnSair = document.getElementById('btnSair');

    areaPart.classList.add('hidden');
    btnSortear.classList.add('hidden');
    btnSair.classList.add('hidden');
    listaEmails.innerHTML = '';

    if (ev.tipo === 'organizador') {
        areaPart.classList.remove('hidden');
        btnSortear.classList.remove('hidden');
        
        const parts = ev.participantes || [];
        document.getElementById('countPart').innerText = parts.length;

        if(parts.length === 0) {
            listaEmails.innerHTML = '<p style="font-size:0.8rem; color:#666; text-align:center; padding:10px;">Ninguém entrou ainda.</p>';
        } else {
            parts.forEach(p => {
                const row = document.createElement('div');
                row.className = 'participant-row';
                row.innerHTML = `
                    <div style="display:flex; flex-direction:column;">
                        <span style="font-weight:600; font-size:0.9rem;">${p.nome}</span>
                        <span style="font-size:0.75rem; color:#94a3b8;">${p.email}</span>
                    </div>
                    <button class="btn-kick" title="Expulsar" onclick="expulsarParticipante(${p.id})">
                        <i class="ph ph-trash"></i>
                    </button>
                `;
                listaEmails.appendChild(row);
            });
        }
    } else {
        btnSair.classList.remove('hidden');
    }
    openModal('modalDetalhes');
}

function copiarCodigoDetalhe() {
    if(eventoSelecionado) {
        navigator.clipboard.writeText(eventoSelecionado.codigoConvite);
        alert("Código copiado!");
    }
}

async function acaoSortear() {
    if(confirm("Deseja realizar o sorteio e enviar os e-mails?")) {
        const res = await fetch(`${API_URL}/sorteio/${eventoSelecionado.codigoConvite}`, { method: 'POST', credentials: 'include' });
        if(res.ok) {
            const msg = await res.text();
            alert("✅ " + msg);
        } else { 
            const t = await res.text(); 
            alert("Erro: " + t); 
        }
    }
}

async function expulsarParticipante(id) {
    if(confirm("Remover este participante?")) {
        const res = await fetch(`${API_URL}/participantes/${id}`, { method: 'DELETE', credentials: 'include' });
        if(res.ok) { alert("Removido."); closeModal('modalDetalhes'); checkSession(); }
        else alert("Erro ao remover.");
    }
}

async function acaoSair() {
    if(confirm("Sair do evento?")) {
        const res = await fetch(`${API_URL}/participantes/sair/${eventoSelecionado.codigoConvite}`, { method: 'DELETE', credentials: 'include' });
        if(res.ok) { alert("Você saiu do evento."); closeModal('modalDetalhes'); checkSession(); }
        else alert("Erro ao sair.");
    }
}

async function criarEvento(e) {
    e.preventDefault();
    const payload = { nomeEvento: document.getElementById('nomeEvento').value, dataSorteio: document.getElementById('dataSorteio').value };
    const res = await fetch(`${API_URL}/eventos`, { method: 'POST', headers: {'Content-Type':'application/json'}, credentials:'include', body:JSON.stringify(payload)});
    if(res.ok) { closeModal('modalNovo'); checkSession(); document.getElementById('formEvento').reset(); } else alert("Erro.");
}

async function entrarEmEvento(e) {
    e.preventDefault();
    const codigo = document.getElementById('joinCodigo').value.toUpperCase();
    const nome = document.getElementById('joinNome').value;
    const gostos = document.getElementById('joinGostos').value;
    
    const res = await fetch(`${API_URL}/participantes/entrar?codigo=${codigo}`, { 
        method: 'POST', 
        headers: {'Content-Type':'application/json'}, 
        credentials:'include', 
        body:JSON.stringify({
            nome: nome,
            gostosPessoais: gostos
        })
    });

    if(res.ok) { 
        alert("Sucesso! Você entrou no evento."); 
        closeModal('modalEntrar'); 
        checkSession(); 
        document.getElementById('formJoin').reset(); 
    } else {
        const msg = await res.text();
        alert("Erro: " + msg); 
    }
}