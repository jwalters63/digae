import { useState } from "react";
import {
  Menu,
  X,
  UserCircle2,
  BarChart3,
  ClipboardCheck,
  Recycle,
  Bell,
  Settings,
  LogOut,
  ChevronRight,
  Leaf,
  Building2,
  FileText,
  Home,
  Search,
} from "lucide-react";

const modules = [
  {
    id: 1,
    title: "Criticidad Ambiental",
    subtitle: "Matrices de aspectos e impactos",
    icon: BarChart3,
    color: "#1B6B3A",
    surface: "#E8F5E9",
    iconBg: "#C8E6C9",
    badge: "12 activos en campus",
    badgeColor: "#2E7D32",
  },
  {
    id: 2,
    title: "Supervisión en Campo",
    subtitle: "Auditorías de infraestructura y energía",
    icon: ClipboardCheck,
    color: "#1A4D7A",
    surface: "#E3F0FB",
    iconBg: "#BBDEFB",
    badge: "3 auditorías globales",
    badgeColor: "#1565C0",
  },
  {
    id: 3,
    title: "Trazabilidad de Residuos",
    subtitle: "Registro y cadena de custodia",
    icon: Recycle,
    color: "#2E6B4A",
    surface: "#E8F5EE",
    iconBg: "#B2DFDB",
    badge: "7 sedes registradas",
    badgeColor: "#00695C",
  },
];

const navItems = [
  { icon: Home, label: "Inicio" },
  { icon: Search, label: "Buscar" },
  { icon: Bell, label: "Alertas" },
  { icon: Settings, label: "Ajustes" },
];

const drawerItems = [
  { icon: Home, label: "Tablero" },
  { icon: BarChart3, label: "Reportes" },
  { icon: Building2, label: "Instalaciones" },
  { icon: FileText, label: "Documentación" },
  { icon: Bell, label: "Notificaciones" },
  { icon: Settings, label: "Configuración" },
];

export default function App() {
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [activeNav, setActiveNav] = useState(0);

  return (
    <div
      className="flex items-center justify-center min-h-screen bg-[#D6E8D8]"
      style={{ fontFamily: "'Roboto', sans-serif" }}
    >
      {/* Phone shell */}
      <div
        className="relative flex flex-col overflow-hidden shadow-2xl"
        style={{
          width: 390,
          height: 844,
          borderRadius: 44,
          background: "var(--background)",
          border: "10px solid #1A1C1A",
          boxShadow:
            "0 0 0 2px #3A3C3A, 0 32px 64px rgba(0,0,0,0.45), inset 0 0 0 1px rgba(255,255,255,0.08)",
        }}
      >
        {/* Status bar */}
        <div
          className="flex items-center justify-between px-7 pt-3 pb-1 flex-shrink-0"
          style={{ background: "#1B6B3A" }}
        >
          <span className="text-white text-xs font-medium" style={{ fontFamily: "'Roboto', sans-serif" }}>
            9:41
          </span>
          <div
            className="w-24 h-5 rounded-full bg-black absolute"
            style={{ left: "50%", transform: "translateX(-50%)", top: 10 }}
          />
          <div className="flex items-center gap-1">
            <svg width="16" height="12" viewBox="0 0 16 12" fill="white">
              <rect x="0" y="4" width="3" height="8" rx="0.5" />
              <rect x="4.5" y="2.5" width="3" height="9.5" rx="0.5" />
              <rect x="9" y="0.5" width="3" height="11.5" rx="0.5" />
              <rect x="13.5" y="0" width="2.5" height="12" rx="0.5" opacity="0.3" />
            </svg>
            <svg width="16" height="12" viewBox="0 0 16 12" fill="white">
              <path d="M8 2C10.5 2 12.7 3.1 14.2 4.8L15.5 3.5C13.6 1.3 10.9 0 8 0C5.1 0 2.4 1.3 0.5 3.5L1.8 4.8C3.3 3.1 5.5 2 8 2Z"/>
              <path d="M8 5C9.7 5 11.2 5.7 12.3 6.8L13.6 5.5C12.1 4 10.2 3 8 3C5.8 3 3.9 4 2.4 5.5L3.7 6.8C4.8 5.7 6.3 5 8 5Z"/>
              <circle cx="8" cy="9.5" r="1.5"/>
            </svg>
            <svg width="25" height="12" viewBox="0 0 25 12" fill="none">
              <rect x="0.5" y="0.5" width="21" height="11" rx="3.5" stroke="white" strokeOpacity="0.4"/>
              <rect x="2" y="2" width="16" height="8" rx="2" fill="white"/>
              <path d="M23 4.5V7.5C23.8 7.2 24.5 6.7 24.5 6C24.5 5.3 23.8 4.8 23 4.5Z" fill="white" opacity="0.4"/>
            </svg>
          </div>
        </div>

        {/* Top App Bar */}
        <div
          className="flex items-center px-3 py-2 flex-shrink-0"
          style={{ background: "#1B6B3A" }}
        >
          <button
            className="w-10 h-10 flex items-center justify-center rounded-full transition-colors hover:bg-white/10 active:bg-white/20"
            onClick={() => setDrawerOpen(true)}
          >
            <Menu size={24} color="white" />
          </button>

          <div className="flex-1 flex items-center justify-center gap-2">
            <Leaf size={18} color="#A5D6A7" />
            <span
              className="text-white font-medium tracking-wider"
              style={{ fontSize: 18, letterSpacing: "0.08em" }}
            >
              SIG DIGAE
            </span>
          </div>

          <button className="w-10 h-10 flex items-center justify-center rounded-full transition-colors hover:bg-white/10 active:bg-white/20">
            <UserCircle2 size={26} color="white" />
          </button>
        </div>

        {/* Scrollable content */}
        <div
          className="flex-1 overflow-y-auto"
          style={{
            scrollbarWidth: "none",
            msOverflowStyle: "none",
          }}
        >
          {/* Header / User greeting */}
          <div
            className="px-4 pt-5 pb-6"
            style={{ background: "#1B6B3A" }}
          >
            <div
              className="rounded-2xl p-4 flex flex-col gap-3"
              style={{
                background: "rgba(255,255,255,0.12)",
                backdropFilter: "blur(8px)",
                border: "1px solid rgba(255,255,255,0.18)",
              }}
            >
              <div className="flex items-center gap-3">
                <div
                  className="w-12 h-12 rounded-full flex items-center justify-center flex-shrink-0"
                  style={{ background: "#A5D6A7" }}
                >
                  <span
                    className="font-medium text-lg"
                    style={{ color: "#1B5E20", fontFamily: "'Roboto', sans-serif" }}
                  >
                    AV
                  </span>
                </div>
                <div className="flex flex-col gap-0.5">
                  <span
                    className="text-white font-normal"
                    style={{ fontSize: 13, opacity: 0.85 }}
                  >
                    Bienvenida,
                  </span>
                  <span
                    className="text-white font-medium"
                    style={{ fontSize: 20, lineHeight: 1.2 }}
                  >
                    Andrea Vargas
                  </span>
                </div>
              </div>

              <div className="flex items-center gap-2 flex-wrap">
                {/* Role chip */}
                <div
                  className="flex items-center gap-1.5 px-3 py-1 rounded-full"
                  style={{ background: "#2E7D32", border: "1px solid #4CAF50" }}
                >
                  <div
                    className="w-1.5 h-1.5 rounded-full"
                    style={{ background: "#A5D6A7" }}
                  />
                  <span
                    className="text-white"
                    style={{ fontSize: 11, fontWeight: 500, letterSpacing: "0.04em" }}
                  >
                    ADMINISTRADORA
                  </span>
                </div>
                {/* Area chip */}
                <div
                  className="flex items-center gap-1.5 px-3 py-1 rounded-full"
                  style={{
                    background: "rgba(255,255,255,0.12)",
                    border: "1px solid rgba(255,255,255,0.25)",
                  }}
                >
                  <Building2 size={11} color="rgba(255,255,255,0.85)" />
                  <span
                    className="text-white"
                    style={{ fontSize: 11, fontWeight: 400, opacity: 0.9, letterSpacing: "0.02em" }}
                  >
                    Dirección DIGAE · Acceso Global
                  </span>
                </div>
              </div>

            </div>
          </div>

          {/* Wave separator */}
          <div style={{ background: "#1B6B3A", marginBottom: -1 }}>
            <svg viewBox="0 0 390 28" preserveAspectRatio="none" style={{ display: "block", width: "100%", height: 28 }}>
              <path d="M0 0 Q97.5 28 195 14 Q292.5 0 390 28 L390 28 L0 28 Z" fill="#F4F7F4" />
            </svg>
          </div>

          {/* Modules section */}
          <div className="px-4 pb-4 pt-3" style={{ background: "#F4F7F4" }}>
            <div className="flex items-center justify-between mb-4">
              <span
                className="font-medium"
                style={{ fontSize: 13, color: "#5A6E5C", letterSpacing: "0.08em", textTransform: "uppercase" }}
              >
                Módulos del Sistema
              </span>
              <button
                className="flex items-center gap-0.5 transition-opacity hover:opacity-70"
                style={{ color: "#1B6B3A", fontSize: 12, fontWeight: 500 }}
              >
                Ver todos <ChevronRight size={14} />
              </button>
            </div>

            {/* Module cards — 2-column grid */}
            <div className="grid grid-cols-2 gap-3 mb-3">
              {modules.slice(0, 2).map((mod) => {
                const Icon = mod.icon;
                return (
                  <button
                    key={mod.id}
                    className="flex flex-col items-center gap-3 p-4 rounded-2xl transition-transform active:scale-95"
                    style={{
                      background: "#FFFFFF",
                      boxShadow: "0 1px 3px rgba(0,0,0,0.08), 0 4px 12px rgba(0,0,0,0.06)",
                      border: "1px solid rgba(27,107,58,0.10)",
                    }}
                  >
                    <div
                      className="w-14 h-14 rounded-2xl flex items-center justify-center"
                      style={{ background: mod.iconBg }}
                    >
                      <Icon size={28} color={mod.color} strokeWidth={1.75} />
                    </div>
                    <div className="flex flex-col items-center gap-1 text-center">
                      <span
                        className="font-medium leading-tight"
                        style={{ fontSize: 13, color: "#1A1C1A" }}
                      >
                        {mod.title}
                      </span>
                      <span
                        className="leading-snug"
                        style={{ fontSize: 11, color: "#5A6E5C" }}
                      >
                        {mod.subtitle}
                      </span>
                    </div>
                    <div
                      className="px-2.5 py-0.5 rounded-full"
                      style={{ background: mod.surface }}
                    >
                      <span style={{ fontSize: 10, color: mod.badgeColor, fontWeight: 500 }}>
                        {mod.badge}
                      </span>
                    </div>
                  </button>
                );
              })}
            </div>

            {/* Third card — full width */}
            {(() => {
              const mod = modules[2];
              const Icon = mod.icon;
              return (
                <button
                  className="w-full flex items-center gap-4 p-4 rounded-2xl transition-transform active:scale-[0.98]"
                  style={{
                    background: "#FFFFFF",
                    boxShadow: "0 1px 3px rgba(0,0,0,0.08), 0 4px 12px rgba(0,0,0,0.06)",
                    border: "1px solid rgba(27,107,58,0.10)",
                  }}
                >
                  <div
                    className="w-14 h-14 rounded-2xl flex items-center justify-center flex-shrink-0"
                    style={{ background: mod.iconBg }}
                  >
                    <Icon size={28} color={mod.color} strokeWidth={1.75} />
                  </div>
                  <div className="flex flex-col items-start gap-1 flex-1 min-w-0">
                    <span className="font-medium" style={{ fontSize: 14, color: "#1A1C1A" }}>
                      {mod.title}
                    </span>
                    <span style={{ fontSize: 12, color: "#5A6E5C" }}>
                      {mod.subtitle}
                    </span>
                    <div
                      className="mt-0.5 px-2.5 py-0.5 rounded-full"
                      style={{ background: mod.surface }}
                    >
                      <span style={{ fontSize: 10, color: mod.badgeColor, fontWeight: 500 }}>
                        {mod.badge}
                      </span>
                    </div>
                  </div>
                  <ChevronRight size={18} color="#8EAD94" />
                </button>
              );
            })()}

            {/* Recent activity section */}
            <div className="mt-5">
              <span
                className="font-medium block mb-3"
                style={{ fontSize: 13, color: "#5A6E5C", letterSpacing: "0.08em", textTransform: "uppercase" }}
              >
                Actividad Reciente
              </span>
              <div
                className="rounded-2xl overflow-hidden"
                style={{
                  background: "#FFFFFF",
                  border: "1px solid rgba(27,107,58,0.10)",
                  boxShadow: "0 1px 3px rgba(0,0,0,0.06)",
                }}
              >
                {[
                  {
                    text: "Matriz de impactos actualizada — Facultad de Ingeniería",
                    time: "Hace 2 min",
                    dot: "#1B6B3A",
                  },
                  {
                    text: "Auditoría energética programada — Facultad de Medicina",
                    time: "Hace 1 h",
                    dot: "#1A4D7A",
                  },
                  {
                    text: "Registro de residuos peligrosos enviado — Edificio Administrativo",
                    time: "Hace 3 h",
                    dot: "#2E6B4A",
                  },
                ].map((item, i, arr) => (
                  <div
                    key={i}
                    className="flex items-center gap-3 px-4 py-3"
                    style={{
                      borderBottom: i < arr.length - 1 ? "1px solid rgba(27,107,58,0.08)" : "none",
                    }}
                  >
                    <div
                      className="w-2 h-2 rounded-full flex-shrink-0"
                      style={{ background: item.dot }}
                    />
                    <span className="flex-1 text-sm" style={{ color: "#1A1C1A", fontSize: 13 }}>
                      {item.text}
                    </span>
                    <span style={{ fontSize: 11, color: "#8EAD94", flexShrink: 0 }}>
                      {item.time}
                    </span>
                  </div>
                ))}
              </div>
            </div>

            {/* Bottom spacer for nav bar */}
            <div className="h-20" />
          </div>
        </div>

        {/* Bottom Navigation Bar */}
        <div
          className="absolute bottom-0 left-0 right-0 flex items-center justify-around px-2 pb-2 pt-1 flex-shrink-0"
          style={{
            background: "#FFFFFF",
            borderTop: "1px solid rgba(27,107,58,0.12)",
            boxShadow: "0 -2px 12px rgba(0,0,0,0.06)",
          }}
        >
          {navItems.map((item, i) => {
            const Icon = item.icon;
            const active = activeNav === i;
            return (
              <button
                key={i}
                className="flex flex-col items-center gap-0.5 px-4 py-2 rounded-xl transition-all"
                style={{
                  background: active ? "#E8F5E9" : "transparent",
                  minWidth: 56,
                }}
                onClick={() => setActiveNav(i)}
              >
                <Icon
                  size={22}
                  color={active ? "#1B6B3A" : "#8EAD94"}
                  strokeWidth={active ? 2 : 1.5}
                />
                <span
                  style={{
                    fontSize: 10,
                    color: active ? "#1B6B3A" : "#8EAD94",
                    fontWeight: active ? 500 : 400,
                  }}
                >
                  {item.label}
                </span>
              </button>
            );
          })}
        </div>

        {/* Navigation Drawer overlay */}
        {drawerOpen && (
          <div
            className="absolute inset-0 z-50 flex"
            style={{ background: "rgba(0,0,0,0.45)" }}
            onClick={() => setDrawerOpen(false)}
          >
            <div
              className="h-full flex flex-col"
              style={{
                width: 280,
                background: "#FFFFFF",
                borderRadius: "0 28px 28px 0",
                boxShadow: "4px 0 24px rgba(0,0,0,0.18)",
              }}
              onClick={(e) => e.stopPropagation()}
            >
              {/* Drawer header */}
              <div
                className="px-5 pt-12 pb-5 flex flex-col gap-3"
                style={{ background: "#1B6B3A", borderRadius: "0 28px 0 0" }}
              >
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <Leaf size={16} color="#A5D6A7" />
                    <span
                      className="text-white font-medium tracking-wider"
                      style={{ fontSize: 16, letterSpacing: "0.06em" }}
                    >
                      SIG DIGAE
                    </span>
                  </div>
                  <button
                    className="w-8 h-8 flex items-center justify-center rounded-full hover:bg-white/10"
                    onClick={() => setDrawerOpen(false)}
                  >
                    <X size={18} color="white" />
                  </button>
                </div>
                <div className="flex items-center gap-3">
                  <div
                    className="w-10 h-10 rounded-full flex items-center justify-center"
                    style={{ background: "#A5D6A7" }}
                  >
                    <span className="font-medium" style={{ color: "#1B5E20", fontSize: 15 }}>AV</span>
                  </div>
                  <div>
                    <p className="text-white font-medium" style={{ fontSize: 14 }}>Andrea Vargas</p>
                    <p style={{ fontSize: 11, color: "rgba(255,255,255,0.75)" }}>a.vargas@digae.edu.co</p>
                  </div>
                </div>
              </div>

              {/* Drawer items */}
              <div className="flex-1 py-2">
                {drawerItems.map((item, i) => {
                  const Icon = item.icon;
                  const active = i === 0;
                  return (
                    <button
                      key={i}
                      className="w-full flex items-center gap-4 px-5 py-3 transition-colors hover:bg-[#E8F5E9]"
                      style={{
                        background: active ? "#E8F5E9" : "transparent",
                        borderRadius: active ? 100 : 0,
                      }}
                    >
                      <Icon
                        size={20}
                        color={active ? "#1B6B3A" : "#5A6E5C"}
                        strokeWidth={active ? 2 : 1.5}
                      />
                      <span
                        style={{
                          fontSize: 14,
                          color: active ? "#1B6B3A" : "#1A1C1A",
                          fontWeight: active ? 500 : 400,
                        }}
                      >
                        {item.label}
                      </span>
                    </button>
                  );
                })}
              </div>

              {/* Logout */}
              <div style={{ borderTop: "1px solid rgba(27,107,58,0.12)", padding: "12px 20px 24px" }}>
                <button className="w-full flex items-center gap-4 px-4 py-3 rounded-xl hover:bg-red-50 transition-colors">
                  <LogOut size={20} color="#BA1A1A" strokeWidth={1.5} />
                  <span style={{ fontSize: 14, color: "#BA1A1A" }}>Cerrar sesión</span>
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
