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

function fazerLoginGoogle() { window.location.href = `${API_URL.replace('/api', '')}/oauth2/authorization/google`; }
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
            todosEventos = [...meusEventos.map(e => ({...e, tipo: 'organizador'})), ...participando.map(e => ({...e, tipo: 'participante'}))];
            filtrarLista('todos');
            showScreen('screen-dashboard');
        } else { showScreen('screen-login'); }
    } catch (e) { showScreen('screen-login'); }
}

function filtrarLista(filtro) {
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.classList.remove('active');
        if(btn.innerText.toLowerCase() === filtro || (filtro === 'todos' && btn.innerText === 'Todos')) btn.classList.add('active');
    });
    renderizarListaUnificada(filtro === 'todos' ? todosEventos : todosEventos.filter(ev => ev.tipo === filtro));
}

function renderizarListaUnificada(lista) {
    const container = document.getElementById('listaEventosUnified');
    container.innerHTML = lista.length ? '' : '<p class="empty-msg">Nenhum evento.</p>';
    lista.forEach(ev => {
        const div = document.createElement('div');
        div.className = 'event-item';
        div.onclick = () => abrirDetalhes(ev);
        div.innerHTML = `<div><h4>${ev.nomeEvento}</h4><span>${ev.codigoConvite}</span></div><div class="badge ${ev.tipo === 'organizador' ? 'badge-org' : 'badge-part'}">${ev.tipo}</div>`;
        container.appendChild(div);
    });
}

function abrirDetalhes(ev) {
    eventoSelecionado = ev;
    document.getElementById('detalheTitulo').innerText = ev.nomeEvento;
    document.getElementById('detalheCodigo').innerText = ev.codigoConvite;
    const isOrg = ev.tipo === 'organizador';

    document.getElementById('areaParticipantes').classList.toggle('hidden', !isOrg);
    document.getElementById('btnSortear').classList.toggle('hidden', !isOrg);
    document.getElementById('btnDeletarEvento').classList.toggle('hidden', !isOrg);
    document.getElementById('btnSair').classList.toggle('hidden', isOrg);

    const lista = document.getElementById('listaEmails');
    lista.innerHTML = '';

    if (isOrg) {
        const parts = ev.participantes || [];
        document.getElementById('countPart').innerText = parts.length;
        parts.forEach(p => {
            const row = document.createElement('div');
            row.className = 'participant-row';
            row.innerHTML = `<span>${p.nome}</span><button class="btn-kick" onclick="expulsarParticipante(${p.id})"><i class="ph ph-trash"></i></button>`;
            lista.appendChild(row);
        });
    }
    openModal('modalDetalhes');
}

async function expulsarParticipante(id) {
    if(confirm("Remover participante?")) {
        const res = await fetch(`${API_URL}/participantes/${id}`, { method: 'DELETE', credentials: 'include' });
        if(res.ok) { alert("Removido."); closeModal('modalDetalhes'); checkSession(); }
    }
}

async function excluirEventoInteiro() {
    if(confirm("Excluir evento permanentemente? Todos os dados serão apagados.")) {
        const res = await fetch(`${API_URL}/eventos/${eventoSelecionado.id}`, { method: 'DELETE', credentials: 'include' });
        if(res.ok) { alert("Evento excluído."); closeModal('modalDetalhes'); checkSession(); }
    }
}

async function acaoSair() {
    if(confirm("Sair do evento?")) {
        const res = await fetch(`${API_URL}/participantes/sair/${eventoSelecionado.codigoConvite}`, { method: 'DELETE', credentials: 'include' });
        if(res.ok) { closeModal('modalDetalhes'); checkSession(); }
    }
}

async function acaoSortear() {
    if(confirm("Realizar sorteio?")) {
        const res = await fetch(`${API_URL}/sorteio/${eventoSelecionado.codigoConvite}`, { method: 'POST', credentials: 'include' });
        if(res.ok) alert("Sorteio concluído!");
    }
}

async function criarEvento(e) {
    e.preventDefault();
    const payload = { nomeEvento: document.getElementById('nomeEvento').value, dataSorteio: document.getElementById('dataSorteio').value };
    const res = await fetch(`${API_URL}/eventos`, { method: 'POST', headers: {'Content-Type':'application/json'}, credentials:'include', body:JSON.stringify(payload)});
    if(res.ok) { closeModal('modalNovo'); checkSession(); }
}

async function entrarEmEvento(e) {
    e.preventDefault();
    const res = await fetch(`${API_URL}/participantes/entrar?codigo=${document.getElementById('joinCodigo').value.toUpperCase()}`, {
        method: 'POST', headers: {'Content-Type':'application/json'}, credentials:'include',
        body:JSON.stringify({ nome: document.getElementById('joinNome').value, gostosPessoais: document.getElementById('joinGostos').value })
    });
    if(res.ok) { closeModal('modalEntrar'); checkSession(); }
}

function copiarCodigoDetalhe() { navigator.clipboard.writeText(eventoSelecionado.codigoConvite); alert("Copiado!"); }