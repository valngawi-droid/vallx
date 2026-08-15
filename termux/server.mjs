import { createServer } from 'node:http';
import { readFile, writeFile } from 'node:fs/promises';
import { existsSync } from 'node:fs';
import { join } from 'node:path';

const port = Number(process.env.PORT || 3000);
const dbFile = join(process.cwd(), 'chipapp-messages.json');
if (!existsSync(dbFile)) await writeFile(dbFile, '[]');
const json = (res, code, data) => { res.writeHead(code, {'Content-Type':'application/json'}); res.end(JSON.stringify(data)); };

createServer(async (req,res) => {
  const url = new URL(req.url, `http://${req.headers.host}`);
  if (url.pathname === '/health') return json(res,200,{ok:true,name:'ChipApp Termux Server',time:Date.now()});
  if (url.pathname === '/messages' && req.method === 'GET') {
    const all = JSON.parse(await readFile(dbFile,'utf8'));
    return json(res,200,all.filter(m => !url.searchParams.get('room') || m.room === url.searchParams.get('room')));
  }
  if (url.pathname === '/messages' && req.method === 'POST') {
    let raw=''; for await (const chunk of req) raw += chunk;
    try {
      const message=JSON.parse(raw); message.id=Date.now();
      const all=JSON.parse(await readFile(dbFile,'utf8')); all.push(message);
      await writeFile(dbFile,JSON.stringify(all,null,2)); return json(res,201,message);
    } catch { return json(res,400,{error:'Data pesan tidak valid'}); }
  }
  json(res,404,{error:'Not found'});
}).listen(port,'0.0.0.0',()=>console.log(`ChipApp Server aktif di http://0.0.0.0:${port}`));
