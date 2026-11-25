# Cyber Security Zero To Hero — Episode 1: Roadmap

> Channel: NexDeve · ভাষা: বাংলা + ইংরেজি technical শব্দ · ফরম্যাট: face cam + screen
> (এগুলো ধরে নিয়ে লেখা। ভুল হলে বলুন, বদলে দেব।)

---

## ১. Hook (প্রথম ১৫ সেকেন্ড)

**Hook A — The Mistake** (hookscore: 60, WORKABLE) ✅ বাছাই করা
> "Random hacking tutorial দেখা বন্ধ করুন। এতে আপনার মাসের পর মাস নষ্ট হচ্ছে — আর কেন, সেটা এই roadmap-এর ২ নম্বর ধাপে বুঝবেন।"

**Hook B — The Reveal** (hookscore: 51)
> "Cyber Security ভুলভাবে শুরু করলে পুরো একটা বছর নষ্ট হবে। আজ দেখাবো ১০ ধাপের সেই পথ, যেটাতে হবে না।"

কেন A: নতুনরা ঠিক এই ভুলটাই করে (এলোমেলো ভিডিও দেখে)। আর "২ নম্বর ধাপ" দর্শককে ভিডিওর ভেতরে ধরে রাখে।

---

## ২. Script

**[0:00–0:15] HOOK**
[ON SCREEN: আপনার মুখ, close-up। পেছনে দ্রুত স্ক্রল হওয়া এলোমেলো "hacking" thumbnail-এর collage, তারপর লাল ক্রস]
> Random hacking tutorial দেখা বন্ধ করুন। এতে আপনার মাসের পর মাস নষ্ট হচ্ছে — আর কেন, সেটা এই roadmap-এর ২ নম্বর ধাপে বুঝবেন।

**[0:15–0:45] THE TURN**
[ON SCREEN: স্ক্রিনে ১০টা ধাপের roadmap, সব ঝাপসা, শুধু "ধাপ ১" উজ্জ্বল]
> এই ভিডিওতে আমি আপনাকে পুরো Cyber Security Zero To Hero কোর্সের ম্যাপ দেখাবো — শূন্য থেকে শুরু করে কোন ধাপের পর কোন ধাপ। প্রতিটা ধাপের জন্য এই সিরিজে আলাদা ভিডিও আসবে। চলুন সরাসরি ধাপ ১ দিয়ে শুরু করি।

**[0:45–1:45] ধাপ ১ — Cyber Security আসলে কী**
[ON SCREEN: তিনটা বক্স — Red Team (attack), Blue Team (defend), GRC (rules & policy)]
> Cyber Security মানে শুধু "hacking" না। মূলত তিনটা দিক আছে। Red Team — যারা অনুমতি নিয়ে system-এ দুর্বলতা খোঁজে। Blue Team — যারা system পাহারা দেয়, attack ধরে। আর GRC — যারা নিয়ম আর policy ঠিক করে। নতুন অবস্থায় আপনাকে এখনই একটা বেছে নিতে হবে না। কিন্তু জেনে রাখুন, চাকরির বেশিরভাগ সুযোগ শুধু Red Team-এ না।

**[1:45–3:00] ধাপ ২ — Foundation (এটাই সেই কারণ)**
[ON SCREEN: একটা বাড়ির ছবি — নিচে "Networking + OS + Linux" লেখা ভিত্তি, উপরে "Hacking tools"]
> এখানেই বেশিরভাগ মানুষ ভুল করে। তারা সরাসরি tool শেখা শুরু করে — Nmap, Metasploit। কিন্তু আপনি যদি না জানেন IP address কী, port কী, packet কীভাবে যায় — তাহলে tool কী output দিচ্ছে তা বুঝবেনই না। ভিত্তি ছাড়া বাড়ি দাঁড়ায় না। তাই random tutorial দেখে লাভ হয় না — ওগুলো মাঝখান থেকে শুরু করে।

**[3:00–4:00] ধাপ ৩ — Networking**
[ON SCREEN: দুইটা কম্পিউটার, মাঝে packet যাচ্ছে; IP, Port, TCP/UDP, DNS লেবেল]
> প্রথমে Networking। IP, Port, TCP আর UDP-র পার্থক্য, DNS কীভাবে কাজ করে। এই সিরিজের পরের ভিডিওগুলো এখান থেকেই শুরু হবে।

**[4:00–5:00] ধাপ ৪ — Linux ও Lab Setup**
[ON SCREEN: screen recording — VirtualBox খোলা, ভেতরে Kali Linux]
> এরপর Linux। আর সবচেয়ে জরুরি — নিজের একটা lab। নিজের কম্পিউটারে VirtualBox দিয়ে Kali Linux আর একটা practice machine। মনে রাখবেন: আমরা শুধু নিজের lab-এ বা অনুমতি আছে এমন জায়গায় practice করবো। অন্যের system-এ অনুমতি ছাড়া চেষ্টা করা অপরাধ।

**[5:00–6:00] ধাপ ৫ ও ৬ — Web আর Vulnerabilities**
[ON SCREEN: browser → server diagram; তারপর OWASP Top 10 লিস্ট]
> তারপর বুঝবো Web কীভাবে কাজ করে — HTTP, cookie, Burp Suite। এরপর OWASP Top 10 — web-এর সবচেয়ে কমন দুর্বলতাগুলো, DVWA-এর মতো practice app-এ হাতে-কলমে।

**[6:00–7:00] ধাপ ৭ ও ৮ — Password, Encryption, আর CTF**
[ON SCREEN: hash-এর উদাহরণ; তারপর TryHackMe / HackTheBox dashboard]
> Password কীভাবে store হয়, hashing আর encryption-এর পার্থক্য। তারপর CTF — TryHackMe, HackTheBox — যেখানে আইনসম্মতভাবে নিজের skill পরীক্ষা করা যায়।

**[7:00–8:00] ধাপ ৯ — Blue Team দিক**
[ON SCREEN: SOC dashboard, log লাইনের scroll]
> শুধু attack জানলে হবে না। Log পড়া, firewall, SOC-এ কী হয় — defensive দিকটাও শিখবো।

**[8:00–9:00] ধাপ ১০ — Portfolio ও Career**
[ON SCREEN: GitHub profile, writeup blog, certification নামগুলো]
> শেষে — নিজের কাজ দেখানো। CTF writeup, GitHub, আর কোন certification দিয়ে শুরু করা যায়। কোনো চাকরির guarantee আমি দেবো না — কিন্তু এই পথে গেলে আপনার কাছে দেখানোর মতো কাজ থাকবে।

**[9:00–9:30] PAYOFF**
[ON SCREEN: পুরো ১০ ধাপের roadmap এবার পরিষ্কার, সব উজ্জ্বল]
> এই হলো পুরো roadmap। আর ২ নম্বর ধাপটাই মূল কথা — ভিত্তি আগে, tool পরে। পুরো roadmap-টা description-এ দেওয়া আছে।

**[9:30–9:45] CLOSE (একটাই ask)**
[ON SCREEN: পরের ভিডিওর thumbnail — "Networking"]
> পরের ভিডিওতে ধাপ ৩ — Networking, একদম শূন্য থেকে। সিরিজটা যেন মিস না হয়, playlist-টা save করে রাখুন।

**আনুমানিক দৈর্ঘ্য:** ~৯–১০ মিনিট।

---

## ৩. Title ও Thumbnail

`title.py` দিয়ে ১০টা title যাচাই করা হয়েছে। সেরা তিনটা:

| Title | Score | সমস্যা |
| --- | --- | --- |
| **Cyber Security শিখবেন? শুরু করুন এখান থেকে (Ep 1)** ✅ | 94 | mobile-এ শেষ অংশ কাটা পড়ে, কিন্তু মূল বিষয় দেখা যায় |
| 10 Steps to Learn Cyber Security From Zero (Ep 1) | 94 | একই |
| Cyber Security Career Roadmap 2026 \| Bangla Ep 1 | 94 | একই |

বাদ পড়েছে: "...Complete Roadmap" (অস্পষ্ট শব্দ), "Don't Learn Cyber Security Before Watching This" (কোনো সংখ্যা বা নাম নেই)।

**Thumbnail brief (বাছাই করা title-এর সাথে):**
- **লেখা (৩ শব্দ):** `ZERO → HERO`
- **মুখ:** আত্মবিশ্বাসী, হালকা হাসি, ক্যামেরার দিকে সরাসরি
- **ফ্রেম:** বাঁয়ে আপনি, ডানে একটা সিঁড়ি বা roadmap যেটা উপরের দিকে উঠছে
- **Background:** গাঢ় নেভি/কালো, সবুজ "terminal" আভা — লেখা সাদা/নিয়ন সবুজ, যাতে ছোট আকারেও পড়া যায়
- title-এর শব্দ thumbnail-এ আবার লিখবেন না।

---

## ৪. Description (কপি করুন)

```
Cyber Security শূন্য থেকে শুরু করতে চান? এই ভিডিওতে পুরো Zero To Hero কোর্সের ১০ ধাপের roadmap।

⏱ Chapters
0:00 কেন random tutorial কাজ করে না
0:45 ধাপ ১ — Cyber Security কী
1:45 ধাপ ২ — Foundation
3:00 ধাপ ৩ — Networking
4:00 ধাপ ৪ — Linux ও Lab Setup
5:00 ধাপ ৫–৬ — Web ও OWASP Top 10
6:00 ধাপ ৭–৮ — Password, Encryption, CTF
7:00 ধাপ ৯ — Blue Team
8:00 ধাপ ১০ — Portfolio ও Career
9:00 পুরো roadmap

📌 Roadmap:
1. Cyber Security কী
2. Foundation
3. Networking
4. Linux + Lab
5. Web basics
6. OWASP Top 10
7. Password & Encryption
8. CTF practice
9. Blue Team
10. Portfolio & Career

⚠️ এই কোর্স শুধু শিক্ষার জন্য। নিজের lab বা অনুমতিপ্রাপ্ত system ছাড়া কোথাও practice করবেন না।

#CyberSecurity #EthicalHacking #NexDeve
```

---

**Ship it, or change it?**
