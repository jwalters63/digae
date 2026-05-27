--
-- PostgreSQL database dump
--

\restrict UwCC6fPgUshR8SIrqa2lMCBRNPZ454MJSNWng5zo96obyXVRS4kqGDX6SELP024

-- Dumped from database version 18.4
-- Dumped by pg_dump version 18.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Data for Name: usuario; Type: TABLE DATA; Schema: public; Owner: jwalters
--

COPY public.usuario (id, nombre, correo, contrasena_hash, rol, activo, fecha_creacion) FROM stdin;
\.


--
-- Data for Name: administrador; Type: TABLE DATA; Schema: public; Owner: jwalters
--

COPY public.administrador (id, nivel_acceso, area_cargo) FROM stdin;
\.


--
-- Data for Name: matriz_aspectos; Type: TABLE DATA; Schema: public; Owner: jwalters
--

COPY public.matriz_aspectos (id, area, actividad, fecha_registro, creado_por) FROM stdin;
\.


--
-- Data for Name: aspecto_ambiental; Type: TABLE DATA; Schema: public; Owner: jwalters
--

COPY public.aspecto_ambiental (id, matriz_id, descripcion, gravedad, severidad, probabilidad) FROM stdin;
\.


--
-- Data for Name: control_operacional; Type: TABLE DATA; Schema: public; Owner: jwalters
--

COPY public.control_operacional (id, descripcion, nivel_criticidad, tipo_control) FROM stdin;
1e08529b-8a3e-4c28-bf02-d23ff1b9c22f	Capacitación en manejo de residuos sólidos para el personal docente y administrativo.	BAJO	ADMINISTRATIVO
65e57538-8595-45b3-a1f1-9e35cda0c81f	Señalización adecuada de áreas de disposición de residuos y puntos de reciclaje.	BAJO	ADMINISTRATIVO
3f4faa64-4ab3-4007-82a6-956672577faf	Implementación de plan de separación en la fuente (orgánico, inorgánico, reciclable).	MEDIO	ADMINISTRATIVO
9b52d20a-0db4-4a98-8992-1fb05934233c	Instalación de contenedores diferenciados por tipo de residuo en todas las áreas.	MEDIO	INGENIERIA
879adea7-735a-4f52-9d66-ec8a08b36f10	Contratación de empresa gestora certificada para retiro de residuos peligrosos.	MEDIO	ADMINISTRATIVO
d9f2b82a-b71f-42ee-ae7b-ab4267c6381f	Sustitución de productos químicos de alta toxicidad por alternativas de menor impacto.	CRÍTICO	SUSTITUCIÓN
9eba419b-077e-4f56-af3b-6eb1cc0af35f	Instalación de sistemas de contención secundaria para almacenamiento de sustancias peligrosas.	CRÍTICO	INGENIERIA
637c31c8-36cd-4544-8e78-f696d649fc80	Protocolo de respuesta ante derrames con kit de emergencia ambiental disponible 24/7.	CRÍTICO	EPP
8d9f5b29-0125-4c79-9243-41f10672f89d	Capacitación en manejo de residuos sólidos para el personal docente y administrativo.	BAJO	ADMINISTRATIVO
0a9f3545-adf7-431e-a139-d5dfc36c4aa2	Señalización adecuada de áreas de disposición de residuos y puntos de reciclaje.	BAJO	ADMINISTRATIVO
aba039c4-8e31-4075-8378-27dc2fd236f2	Implementación de plan de separación en la fuente (orgánico, inorgánico, reciclable).	MEDIO	ADMINISTRATIVO
e6e89ec0-9b2e-41a6-9695-de86b50b6932	Instalación de contenedores diferenciados por tipo de residuo en todas las áreas.	MEDIO	INGENIERIA
e65ecf36-0e9b-49a4-aa85-07dd9a9f4274	Contratación de empresa gestora certificada para retiro de residuos peligrosos.	MEDIO	ADMINISTRATIVO
ba20e25e-84b3-493e-a075-af8b636c63d9	Sustitución de productos químicos de alta toxicidad por alternativas de menor impacto.	CRÍTICO	SUSTITUCIÓN
6e50c336-7c23-443c-a96b-01b10c106ba4	Instalación de sistemas de contención secundaria para almacenamiento de sustancias peligrosas.	CRÍTICO	INGENIERIA
0f14eac8-11de-4037-b975-707a68d4fca7	Protocolo de respuesta ante derrames con kit de emergencia ambiental disponible 24/7.	CRÍTICO	EPP
\.


--
-- Data for Name: aspecto_control; Type: TABLE DATA; Schema: public; Owner: jwalters
--

COPY public.aspecto_control (aspecto_id, control_id) FROM stdin;
\.


--
-- Data for Name: firma_digital; Type: TABLE DATA; Schema: public; Owner: jwalters
--

COPY public.firma_digital (id, datos_canvas, fecha_captura, firmante) FROM stdin;
\.


--
-- Data for Name: bitacora_residuos; Type: TABLE DATA; Schema: public; Owner: jwalters
--

COPY public.bitacora_residuos (id, fecha, area, empresa, tipo, firma_id, registrado_por) FROM stdin;
\.


--
-- Data for Name: facultad; Type: TABLE DATA; Schema: public; Owner: jwalters
--

COPY public.facultad (id, nombre) FROM stdin;
e33adbc1-7e3a-464e-b6ec-2b1115da7d87	Facultad de Ingeniería y Arquitectura
8fbe53b2-ca6f-49d6-b057-3be0f7c12e8c	Facultad de Ciencias Jurídicas y Sociales
0d8a4f2d-6687-4f35-843f-96da11905d53	Facultad de Ciencias Económicas y Empresariales
6719e0a9-9479-4424-9e1f-72128c24020d	Facultad de Ciencias de la Salud
f7a4122d-bac5-4a57-a2f7-c0b9ff3aa2be	DIGAE - Dirección de Gestión Ambiental y Energética
39b7dde0-43a4-4297-8e37-5c7e062919b6	Facultad de Ciencias Adminstrativas y Económicas
\.


--
-- Data for Name: supervision; Type: TABLE DATA; Schema: public; Owner: jwalters
--

COPY public.supervision (id, fecha, supervisor, area, estado) FROM stdin;
\.


--
-- Data for Name: item_supervision; Type: TABLE DATA; Schema: public; Owner: jwalters
--

COPY public.item_supervision (id, supervision_id, categoria, descripcion, resultado, observacion) FROM stdin;
\.


--
-- Data for Name: residuo; Type: TABLE DATA; Schema: public; Owner: jwalters
--

COPY public.residuo (id, bitacora_id, tipo, peso_kg, volumen_litros, descripcion) FROM stdin;
\.


--
-- Data for Name: usuario_operativo; Type: TABLE DATA; Schema: public; Owner: jwalters
--

COPY public.usuario_operativo (id, facultad_id, area_asignada) FROM stdin;
\.


--
-- PostgreSQL database dump complete
--

\unrestrict UwCC6fPgUshR8SIrqa2lMCBRNPZ454MJSNWng5zo96obyXVRS4kqGDX6SELP024

