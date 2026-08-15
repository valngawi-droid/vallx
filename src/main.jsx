import React, { useMemo, useRef, useState } from 'react';
import { createRoot } from 'react-dom/client';
import {
  Archive, ArrowLeft, BellOff, Camera, Check, CheckCheck, ChevronRight, CircleDashed,
  EllipsisVertical, FileText, Image as ImageIcon, Info, Link2, LockKeyhole, MessageCircleMore,
  Mic, Paperclip, Phone, PhoneIncoming, PhoneOutgoing, Plus, Search, Send, Settings,
  Smile, Sparkles, SquarePen, Star, UserPlus, Users, Video, Volume2, X
} from 'lucide-react';
import './styles.css';

const people = [
  { id:1, name:'Alya Putri', initials:'AP', color:'#ec7695', time:'10.42', unread:2, online:true, status:'online', preview:'Wah boleh banget! Jam berapa?', mood:'Sore yang tenang ✨' },
  { id:2, name:'Tim Produk', initials:'TP', color:'#5577d9', time:'09.18', unread:4, group:true, status:'8 anggota', preview:'Bima: Mockup barunya keren 🔥' },
  { id:3, name:'Raka Pradana', initials:'RP', color:'#eba94d', time:'Kemarin', preview:'Sip, sampai ketemu besok!', read:true, status:'terakhir dilihat kemarin' },
  { id:4, name:'Mama', initials:'MA', color:'#45a98d', time:'Kemarin', preview:'Jangan lupa makan ya ❤️', status:'online', online:true },
  { id:5, name:'Nadia S.', initials:'NS', color:'#9b6bd5', time:'Senin', preview:'Foto', media:true, status:'terakhir dilihat Senin' },
  { id:6, name:'Weekend Escape', initials:'WE', color:'#d8734e', time:'Minggu', group:true, preview:'Dito: Jadi kita berangkat pagi?', status:'5 anggota' },
  { id:7, name:'Bagas', initials:'BG', color:'#5c88aa', time:'Sabtu', preview:'Oke, noted. Makasih ya!', status:'terakhir dilihat Sabtu' },
];
const seedMessages = {
  1:[{day:'Hari ini'},{from:'them',text:'Hai! Gimana harimu?',time:'10.31'},{from:'me',text:'Lumayan seru, baru selesai meeting panjang 😅',time:'10.33'},{from:'them',text:'Hahaha akhirnya selesai juga. Mau ngopi sore ini?',time:'10.36'},{from:'me',text:'Boleh! Tempat yang kemarin gimana?',time:'10.38'},{from:'them',text:'Wah boleh banget! Jam berapa?',time:'10.42'}],
  2:[{day:'Hari ini'},{from:'them',author:'Bima',text:'Aku sudah upload revisi flow onboarding.',time:'08.46'},{from:'me',text:'Oke, aku cek sebentar ya.',time:'08.52'},{from:'them',author:'Bima',text:'Mockup barunya keren 🔥',time:'09.18'}],
  3:[{day:'Kemarin'},{from:'me',text:'Sip, sampai ketemu besok!',time:'21.07'}],
  4:[{day:'Kemarin'},{from:'them',text:'Jangan lupa makan ya ❤️',time:'18.25'}],
  5:[{day:'Senin'},{from:'them',text:'Aku kirim foto waktu di Bali ya!',time:'16.11'}],
  6:[{day:'Minggu'},{from:'them',author:'Dito',text:'Jadi kita berangkat pagi?',time:'12.20'}],
  7:[{day:'Sabtu'},{from:'them',text:'Oke, noted. Makasih ya!',time:'11.02'}],
};
const statusPeople = [people[0], people[4], people[2], people[3]];

function Avatar({ person, size='md', ring=false }) {
  return <div className={`avatar ${size} ${ring?'ring':''}`} style={{'--c':person.color}}><span>{person.initials}</span>{person.online&&<i/>}</div>;
}
function Logo(){return <div className="logo"><span><MessageCircleMore/></span><b>Chip<span>App</span></b></div>}
function IconButton({children,onClick,className=''}){return <button className={`icon-button ${className}`} onClick={onClick}>{children}</button>}

function App(){
  const [tab,setTab]=useState('chat');
  const [screen,setScreen]=useState('home');
  const [activeId,setActiveId]=useState(1);
  const [messages,setMessages]=useState(seedMessages);
  const [draft,setDraft]=useState('');
  const [search,setSearch]=useState(false);
  const [query,setQuery]=useState('');
  const [filter,setFilter]=useState('Semua');
  const [toast,setToast]=useState('');
  const [emoji,setEmoji]=useState(false);
  const [sheet,setSheet]=useState(false);
  const fileRef=useRef(null);
  const active=people.find(p=>p.id===activeId);
  const notify=(text)=>{setToast(text);clearTimeout(window.toastTimer);window.toastTimer=setTimeout(()=>setToast(''),2200)};
  const openChat=(id)=>{setActiveId(id);setScreen('chat');setSheet(false)};
  const visible=useMemo(()=>people.filter(p=>p.name.toLowerCase().includes(query.toLowerCase())&&(filter==='Semua'||(filter==='Belum dibaca'&&p.unread)||(filter==='Grup'&&p.group))),[query,filter]);
  const send=(e)=>{e?.preventDefault();if(!draft.trim())return;const time=new Date().toLocaleTimeString('id-ID',{hour:'2-digit',minute:'2-digit'}).replace(':','.');setMessages(m=>({...m,[activeId]:[...(m[activeId]||[]),{from:'me',text:draft.trim(),time}]}));setDraft('');setEmoji(false)};

  return <div className="stage"><div className="phone-app">
    {screen==='home' && <>
      <header className="app-header ios-header">
        {search ? <div className="search-mode"><button className="ios-text" onClick={()=>{setSearch(false);setQuery('')}}>Batal</button><input autoFocus value={query} onChange={e=>setQuery(e.target.value)} placeholder="Cari"/></div> : <>
          <button className="ios-text" onClick={()=>notify(tab==='chat'?'Mode edit chat':'Menu lainnya')}>{tab==='chat'?'Edit':''}</button>
          <b className="ios-title">{tab==='chat'?'Chat':tab==='updates'?'Pembaruan':tab==='calls'?'Panggilan':tab==='communities'?'Komunitas':'Pengaturan'}</b>
          <div className="header-tools">{tab==='chat'&&<><IconButton onClick={()=>notify('Kamera dibuka')}><Camera/></IconButton><IconButton onClick={()=>notify('Pilih kontak untuk chat baru')}><SquarePen/></IconButton></>}{tab==='updates'&&<IconButton onClick={()=>notify('Buat status baru')}><Plus/></IconButton>}</div>
        </>}
      </header>
      <div className="home-content">
        {tab==='chat'&&<><div className="ios-search" onClick={()=>setSearch(true)}><Search/><span>Cari</span></div><ChatHome visible={visible} filter={filter} setFilter={setFilter} openChat={openChat} notify={notify}/></>} 
        {tab==='updates'&&<Updates notify={notify}/>} 
        {tab==='communities'&&<Communities notify={notify}/>} 
        {tab==='calls'&&<Calls notify={notify}/>} 
        {tab==='settings'&&<SettingsHome notify={notify}/>} 
      </div>
      <BottomNav tab={tab} setTab={setTab}/>
    </>}

    {screen==='chat'&&<section className="chat-screen">
      <header className="chat-header"><IconButton onClick={()=>setScreen('home')}><ArrowLeft/></IconButton><button className="chat-person" onClick={()=>setScreen('profile')}><Avatar person={active}/><span><b>{active.name}</b><small>{active.status}</small></span></button><IconButton onClick={()=>notify('Panggilan video dimulai')}><Video/></IconButton><IconButton onClick={()=>notify(`Memanggil ${active.name}...`)}><Phone/></IconButton><IconButton onClick={()=>setSheet(true)}><EllipsisVertical/></IconButton></header>
      <div className="chat-wall"><div className="encrypted"><LockKeyhole/> Pesan dan panggilan terenkripsi end-to-end.</div><div className="message-list">{(messages[activeId]||[]).map((m,i)=>m.day?<span className="day" key={i}>{m.day}</span>:<div className={`message-row ${m.from}`} key={i}><div className="message">{m.author&&<strong>{m.author}</strong>}<span>{m.text}</span><time>{m.time}{m.from==='me'&&<CheckCheck/>}</time></div></div>)}</div></div>
      <form className="message-bar" onSubmit={send}><div className="input-pill"><IconButton type="button" onClick={()=>setEmoji(!emoji)}><Smile/></IconButton><textarea rows="1" value={draft} onChange={e=>setDraft(e.target.value)} onKeyDown={e=>{if(e.key==='Enter'&&!e.shiftKey){e.preventDefault();send()}}} placeholder="Pesan"/><IconButton type="button" onClick={()=>fileRef.current?.click()}><Paperclip/></IconButton><IconButton type="button" onClick={()=>notify('Kamera dibuka')}><Camera/></IconButton><input hidden type="file" ref={fileRef} onChange={e=>e.target.files[0]&&notify(`${e.target.files[0].name} siap dikirim`)}/>{emoji&&<div className="emoji-box">{['😀','😂','🥰','😎','❤️','🔥','👍','🎉','🙌','✨'].map(x=><button type="button" key={x} onClick={()=>setDraft(d=>d+x)}>{x}</button>)}</div>}</div><button className="voice-send" type={draft?'submit':'button'} onClick={!draft?()=>notify('Tahan untuk merekam suara'):undefined}>{draft?<Send/>:<Mic/>}</button></form>
      {sheet&&<><div className="scrim" onClick={()=>setSheet(false)}/><div className="bottom-sheet"><span className="grab"/><button onClick={()=>setScreen('profile')}><Info/> Info kontak</button><button onClick={()=>notify('Pencarian pesan dibuka')}><Search/> Cari</button><button onClick={()=>notify('Notifikasi dibisukan')}><BellOff/> Bisukan notifikasi</button><button onClick={()=>notify('Pesan ditandai')}><Star/> Pesan berbintang</button></div></>}
    </section>}

    {screen==='profile'&&<Profile person={active} back={()=>setScreen('chat')} notify={notify}/>} 
    {screen==='settings'&&<SettingsPage back={()=>setScreen('home')} notify={notify}/>} 
    {toast&&<div className="toast"><Sparkles/>{toast}</div>}
  </div></div>
}

function ChatHome({visible,filter,setFilter,openChat,notify}){return <>
  <div className="ios-chat-links"><button onClick={()=>notify('Daftar siaran dibuka')}>Daftar Siaran</button><button onClick={()=>notify('Grup baru dibuat')}>Grup Baru</button></div>
  <div className="filter-row">{['Semua','Belum dibaca','Grup'].map(x=><button key={x} className={filter===x?'active':''} onClick={()=>setFilter(x)}>{x}</button>)}</div>
  <button className="archive" onClick={()=>notify('Tidak ada chat diarsipkan')}><span><Archive/></span><b>Diarsipkan</b><em>0</em></button>
  <div className="chat-list">{visible.map(p=><button className="chat-item" key={p.id} onClick={()=>openChat(p.id)}><Avatar person={p}/><span className="chat-copy"><span><b>{p.name}</b><time className={p.unread?'hot':''}>{p.time}</time></span><span><small>{p.read&&<CheckCheck/>}{p.media&&<ImageIcon/>}{p.preview}</small>{p.unread&&<i>{p.unread}</i>}</span></span></button>)}{!visible.length&&<div className="nothing"><Search/><b>Chat tidak ditemukan</b><small>Coba cari nama yang lain</small></div>}</div>
</>}
function Updates({notify}){return <div className="page-pad"><section className="welcome"><small>Bagikan momenmu</small><h1>Updates</h1></section><h3 className="section-title">Cerita terbaru</h3><div className="story-grid">{statusPeople.map(p=><button key={p.id} style={{'--c':p.color}} onClick={()=>notify(`Melihat cerita ${p.name}`)}><Avatar person={p}/><span><b>{p.name.split(' ')[0]}</b><small>Hari ini</small></span></button>)}</div><div className="channel-title"><h3>Channel</h3><button>Jelajahi</button></div>{['ChipApp News','Tech in 60s','Ruang Kreatif'].map((x,i)=><button className="channel" key={x} onClick={()=>notify(`${x} dibuka`)}><span className={`channel-icon c${i}`}><Volume2/></span><span><b>{x}</b><small>{i+2} update baru</small></span><ChevronRight/></button>)}</div>}
function Communities({notify}){return <div className="page-pad"><section className="welcome"><small>Terhubung bersama</small><h1>Komunitas</h1></section><div className="community-hero"><span><Users/></span><h2>Satu ruang untuk semua</h2><p>Satukan grup yang saling terhubung dan kirim pengumuman dengan mudah.</p><button onClick={()=>notify('Komunitas baru dibuat')}><Plus/> Buat komunitas</button></div><h3 className="section-title">Komunitas kamu</h3><button className="community-row" onClick={()=>notify('Kreatif Lokal dibuka')}><span>KL</span><span><b>Kreatif Lokal</b><small>3 grup • 128 anggota</small></span><ChevronRight/></button></div>}
function Calls({notify}){return <div className="page-pad"><section className="welcome"><small>Tetap dekat</small><h1>Panggilan</h1></section><button className="call-link" onClick={()=>notify('Tautan panggilan disalin')}><span><Link2/></span><span><b>Buat tautan panggilan</b><small>Bagikan tautan untuk panggilan ChipApp</small></span></button><h3 className="section-title">Terbaru</h3>{[people[0],people[2],people[4],people[3]].map((p,i)=><button className="call-row" key={p.id} onClick={()=>notify(`Memanggil ${p.name}...`)}><Avatar person={p}/><span><b>{p.name}</b><small className={i===2?'missed':''}>{i===2?<PhoneIncoming/>:<PhoneOutgoing/>}{i===0?'Hari ini, 10.12':'Kemarin, 20.0'+i}</small></span><Phone/></button>)}</div>}
function BottomNav({tab,setTab}){const nav=[['updates',CircleDashed,'Pembaruan'],['calls',Phone,'Panggilan'],['communities',Users,'Komunitas'],['chat',MessageCircleMore,'Chat'],['settings',Settings,'Pengaturan']];return <nav className="bottom-nav">{nav.map(([id,Icon,label])=><button key={id} className={tab===id?'active':''} onClick={()=>setTab(id)}><span><Icon/>{id==='chat'&&<i>6</i>}</span><small>{label}</small></button>)}</nav>}
function Profile({person,back,notify}){return <section className="sub-page"><header className="simple-header"><IconButton onClick={back}><ArrowLeft/></IconButton><b>Info kontak</b><IconButton><EllipsisVertical/></IconButton></header><div className="profile-top"><Avatar person={person} size="xl"/><h2>{person.name}</h2><p>+62 812 •••• 4812</p><div><button onClick={()=>notify('Panggilan suara dimulai')}><Phone/><small>Audio</small></button><button onClick={()=>notify('Panggilan video dimulai')}><Video/><small>Video</small></button><button onClick={()=>notify('Pencarian dibuka')}><Search/><small>Cari</small></button></div></div><div className="card"><small>Info</small><p>{person.mood||'Jalani hari, nikmati setiap cerita. ✨'}</p></div><div className="card media"><span><small>Media, tautan, dan dokumen</small><b>12 <ChevronRight/></b></span><div><i><ImageIcon/></i><i><FileText/></i><i><Camera/></i></div></div><div className="card options"><button><Star/>Pesan berbintang<ChevronRight/></button><button><BellOff/>Bisukan notifikasi</button><button><LockKeyhole/>Enkripsi<ChevronRight/></button></div></section>}
function SettingsHome({notify}){return <div className="settings-home"><div className="account ios-account"><Avatar person={{initials:'VL',color:'#5c9bea'}} size="lg"/><span><h2>Val</h2><p>Hidup adalah cerita ✨</p></span><ChevronRight/></div><div className="card options ios-options"><button onClick={()=>notify('Pesan berbintang dibuka')}><i className="setting-icon yellow"><Star/></i>Pesan Berbintang<ChevronRight/></button><button onClick={()=>notify('Perangkat tertaut dibuka')}><i className="setting-icon green"><Link2/></i>Perangkat Tertaut<ChevronRight/></button></div><div className="card options ios-options"><button><i className="setting-icon blue"><Settings/></i>Akun<ChevronRight/></button><button><i className="setting-icon teal"><LockKeyhole/></i>Privasi<ChevronRight/></button><button><i className="setting-icon lime"><MessageCircleMore/></i>Chat<ChevronRight/></button><button><i className="setting-icon red"><BellOff/></i>Notifikasi<ChevronRight/></button><button><i className="setting-icon purple"><Info/></i>Penyimpanan dan Data<ChevronRight/></button></div><div className="chip-version"><Logo/><small>Server terhubung • Termux</small></div></div>}
function SettingsPage({back,notify}){return <section className="sub-page settings-page"><header className="simple-header"><IconButton onClick={back}><ArrowLeft/></IconButton><b>Menu</b><span/></header><div className="account"><Avatar person={{initials:'VL',color:'#7055f5'}} size="lg"/><span><h2>Val</h2><p>Hidup adalah cerita ✨</p></span><Camera/></div><div className="card options"><button onClick={()=>notify('Pengaturan dibuka')}><Settings/>Pengaturan<ChevronRight/></button><button><Star/>Pesan berbintang<ChevronRight/></button><button><Archive/>Chat diarsipkan<ChevronRight/></button><button><UserPlus/>Undang teman<ChevronRight/></button></div><div className="chip-footer"><Logo/><small>versi 1.0.0</small></div></section>}

createRoot(document.getElementById('root')).render(<App/>);

if ('serviceWorker' in navigator && import.meta.env.PROD) {
  window.addEventListener('load', () => navigator.serviceWorker.register('/sw.js'));
}
