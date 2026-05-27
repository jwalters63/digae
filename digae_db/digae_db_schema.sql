--
-- PostgreSQL database dump
--

\restrict NuV7J4gdxDZTdVPuhur5M30VZf0ePH9uLhpYuf4yDc1T3zv1phz4x5KF8o0ixF2

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
-- Name: uuid-ossp; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;


--
-- Name: EXTENSION "uuid-ossp"; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION "uuid-ossp" IS 'generate universally unique identifiers (UUIDs)';


--
-- Name: estado_supervision_enum; Type: TYPE; Schema: public; Owner: jwalters
--

CREATE TYPE public.estado_supervision_enum AS ENUM (
    'PENDIENTE',
    'EN_PROGRESO',
    'FINALIZADA'
);


ALTER TYPE public.estado_supervision_enum OWNER TO jwalters;

--
-- Name: nivel_criticidad_enum; Type: TYPE; Schema: public; Owner: jwalters
--

CREATE TYPE public.nivel_criticidad_enum AS ENUM (
    'BAJO',
    'MEDIO',
    'CRÍTICO'
);


ALTER TYPE public.nivel_criticidad_enum OWNER TO jwalters;

--
-- Name: resultado_item_enum; Type: TYPE; Schema: public; Owner: jwalters
--

CREATE TYPE public.resultado_item_enum AS ENUM (
    'CUMPLE',
    'NO_CUMPLE',
    'NO_APLICA'
);


ALTER TYPE public.resultado_item_enum OWNER TO jwalters;

--
-- Name: rol_usuario_enum; Type: TYPE; Schema: public; Owner: jwalters
--

CREATE TYPE public.rol_usuario_enum AS ENUM (
    'ADMINISTRADOR',
    'OPERATIVO'
);


ALTER TYPE public.rol_usuario_enum OWNER TO jwalters;

--
-- Name: tipo_control_enum; Type: TYPE; Schema: public; Owner: jwalters
--

CREATE TYPE public.tipo_control_enum AS ENUM (
    'ADMINISTRATIVO',
    'INGENIERIA',
    'EPP',
    'SUSTITUCIÓN',
    'ELIMINACIÓN'
);


ALTER TYPE public.tipo_control_enum OWNER TO jwalters;

--
-- Name: tipo_residuo_enum; Type: TYPE; Schema: public; Owner: jwalters
--

CREATE TYPE public.tipo_residuo_enum AS ENUM (
    'COMUN',
    'PELIGROSO',
    'BIOINFECCIOSO'
);


ALTER TYPE public.tipo_residuo_enum OWNER TO jwalters;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: administrador; Type: TABLE; Schema: public; Owner: jwalters
--

CREATE TABLE public.administrador (
    id uuid NOT NULL,
    nivel_acceso integer DEFAULT 1 NOT NULL,
    area_cargo character varying(150)
);


ALTER TABLE public.administrador OWNER TO jwalters;

--
-- Name: aspecto_ambiental; Type: TABLE; Schema: public; Owner: jwalters
--

CREATE TABLE public.aspecto_ambiental (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    matriz_id uuid NOT NULL,
    descripcion character varying(500) NOT NULL,
    gravedad integer NOT NULL,
    severidad integer NOT NULL,
    probabilidad integer NOT NULL,
    criticidad integer GENERATED ALWAYS AS (((gravedad * severidad) * probabilidad)) STORED,
    nivel_criticidad public.nivel_criticidad_enum GENERATED ALWAYS AS (
CASE
    WHEN (((gravedad * severidad) * probabilidad) <= 25) THEN 'BAJO'::public.nivel_criticidad_enum
    WHEN (((gravedad * severidad) * probabilidad) <= 75) THEN 'MEDIO'::public.nivel_criticidad_enum
    ELSE 'CRÍTICO'::public.nivel_criticidad_enum
END) STORED,
    CONSTRAINT chk_gravedad CHECK (((gravedad >= 1) AND (gravedad <= 5))),
    CONSTRAINT chk_probabilidad CHECK (((probabilidad >= 1) AND (probabilidad <= 5))),
    CONSTRAINT chk_severidad CHECK (((severidad >= 1) AND (severidad <= 5)))
);


ALTER TABLE public.aspecto_ambiental OWNER TO jwalters;

--
-- Name: aspecto_control; Type: TABLE; Schema: public; Owner: jwalters
--

CREATE TABLE public.aspecto_control (
    aspecto_id uuid NOT NULL,
    control_id uuid NOT NULL
);


ALTER TABLE public.aspecto_control OWNER TO jwalters;

--
-- Name: bitacora_residuos; Type: TABLE; Schema: public; Owner: jwalters
--

CREATE TABLE public.bitacora_residuos (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    fecha timestamp without time zone DEFAULT now() NOT NULL,
    area character varying(150) NOT NULL,
    empresa character varying(200) NOT NULL,
    tipo character varying(100),
    firma_id uuid NOT NULL,
    registrado_por uuid NOT NULL
);


ALTER TABLE public.bitacora_residuos OWNER TO jwalters;

--
-- Name: control_operacional; Type: TABLE; Schema: public; Owner: jwalters
--

CREATE TABLE public.control_operacional (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    descripcion character varying(500) NOT NULL,
    nivel_criticidad public.nivel_criticidad_enum NOT NULL,
    tipo_control public.tipo_control_enum NOT NULL
);


ALTER TABLE public.control_operacional OWNER TO jwalters;

--
-- Name: facultad; Type: TABLE; Schema: public; Owner: jwalters
--

CREATE TABLE public.facultad (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    nombre character varying(150) NOT NULL
);


ALTER TABLE public.facultad OWNER TO jwalters;

--
-- Name: firma_digital; Type: TABLE; Schema: public; Owner: jwalters
--

CREATE TABLE public.firma_digital (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    datos_canvas text NOT NULL,
    fecha_captura timestamp without time zone DEFAULT now() NOT NULL,
    firmante character varying(200) NOT NULL
);


ALTER TABLE public.firma_digital OWNER TO jwalters;

--
-- Name: item_supervision; Type: TABLE; Schema: public; Owner: jwalters
--

CREATE TABLE public.item_supervision (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    supervision_id uuid NOT NULL,
    categoria character varying(100) NOT NULL,
    descripcion character varying(500) NOT NULL,
    resultado public.resultado_item_enum NOT NULL,
    observacion text
);


ALTER TABLE public.item_supervision OWNER TO jwalters;

--
-- Name: matriz_aspectos; Type: TABLE; Schema: public; Owner: jwalters
--

CREATE TABLE public.matriz_aspectos (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    area character varying(100) NOT NULL,
    actividad character varying(250) NOT NULL,
    fecha_registro timestamp without time zone DEFAULT now() NOT NULL,
    creado_por uuid NOT NULL
);


ALTER TABLE public.matriz_aspectos OWNER TO jwalters;

--
-- Name: residuo; Type: TABLE; Schema: public; Owner: jwalters
--

CREATE TABLE public.residuo (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    bitacora_id uuid NOT NULL,
    tipo public.tipo_residuo_enum NOT NULL,
    peso_kg numeric(10,3),
    volumen_litros numeric(10,3),
    descripcion character varying(300),
    CONSTRAINT chk_residuo_medida CHECK (((peso_kg IS NOT NULL) OR (volumen_litros IS NOT NULL)))
);


ALTER TABLE public.residuo OWNER TO jwalters;

--
-- Name: supervision; Type: TABLE; Schema: public; Owner: jwalters
--

CREATE TABLE public.supervision (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    fecha timestamp without time zone DEFAULT now() NOT NULL,
    supervisor uuid NOT NULL,
    area character varying(150) NOT NULL,
    estado public.estado_supervision_enum DEFAULT 'PENDIENTE'::public.estado_supervision_enum NOT NULL
);


ALTER TABLE public.supervision OWNER TO jwalters;

--
-- Name: usuario; Type: TABLE; Schema: public; Owner: jwalters
--

CREATE TABLE public.usuario (
    id uuid DEFAULT public.uuid_generate_v4() NOT NULL,
    nombre character varying(150) NOT NULL,
    correo character varying(200) NOT NULL,
    contrasena_hash character varying(255) NOT NULL,
    rol public.rol_usuario_enum NOT NULL,
    activo boolean DEFAULT true NOT NULL,
    fecha_creacion timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.usuario OWNER TO jwalters;

--
-- Name: usuario_operativo; Type: TABLE; Schema: public; Owner: jwalters
--

CREATE TABLE public.usuario_operativo (
    id uuid NOT NULL,
    facultad_id uuid NOT NULL,
    area_asignada character varying(150)
);


ALTER TABLE public.usuario_operativo OWNER TO jwalters;

--
-- Name: administrador pk_administrador; Type: CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.administrador
    ADD CONSTRAINT pk_administrador PRIMARY KEY (id);


--
-- Name: aspecto_ambiental pk_aspecto_ambiental; Type: CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.aspecto_ambiental
    ADD CONSTRAINT pk_aspecto_ambiental PRIMARY KEY (id);


--
-- Name: aspecto_control pk_aspecto_control; Type: CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.aspecto_control
    ADD CONSTRAINT pk_aspecto_control PRIMARY KEY (aspecto_id, control_id);


--
-- Name: bitacora_residuos pk_bitacora; Type: CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.bitacora_residuos
    ADD CONSTRAINT pk_bitacora PRIMARY KEY (id);


--
-- Name: control_operacional pk_control_operacional; Type: CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.control_operacional
    ADD CONSTRAINT pk_control_operacional PRIMARY KEY (id);


--
-- Name: facultad pk_facultad; Type: CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.facultad
    ADD CONSTRAINT pk_facultad PRIMARY KEY (id);


--
-- Name: firma_digital pk_firma_digital; Type: CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.firma_digital
    ADD CONSTRAINT pk_firma_digital PRIMARY KEY (id);


--
-- Name: item_supervision pk_item_supervision; Type: CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.item_supervision
    ADD CONSTRAINT pk_item_supervision PRIMARY KEY (id);


--
-- Name: matriz_aspectos pk_matriz_aspectos; Type: CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.matriz_aspectos
    ADD CONSTRAINT pk_matriz_aspectos PRIMARY KEY (id);


--
-- Name: residuo pk_residuo; Type: CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.residuo
    ADD CONSTRAINT pk_residuo PRIMARY KEY (id);


--
-- Name: supervision pk_supervision; Type: CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.supervision
    ADD CONSTRAINT pk_supervision PRIMARY KEY (id);


--
-- Name: usuario pk_usuario; Type: CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT pk_usuario PRIMARY KEY (id);


--
-- Name: usuario_operativo pk_usuario_operativo; Type: CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.usuario_operativo
    ADD CONSTRAINT pk_usuario_operativo PRIMARY KEY (id);


--
-- Name: bitacora_residuos uq_bitacora_firma; Type: CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.bitacora_residuos
    ADD CONSTRAINT uq_bitacora_firma UNIQUE (firma_id);


--
-- Name: facultad uq_facultad_nombre; Type: CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.facultad
    ADD CONSTRAINT uq_facultad_nombre UNIQUE (nombre);


--
-- Name: usuario uq_usuario_correo; Type: CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.usuario
    ADD CONSTRAINT uq_usuario_correo UNIQUE (correo);


--
-- Name: idx_aspecto_criticidad; Type: INDEX; Schema: public; Owner: jwalters
--

CREATE INDEX idx_aspecto_criticidad ON public.aspecto_ambiental USING btree (nivel_criticidad);


--
-- Name: idx_aspecto_matriz; Type: INDEX; Schema: public; Owner: jwalters
--

CREATE INDEX idx_aspecto_matriz ON public.aspecto_ambiental USING btree (matriz_id);


--
-- Name: idx_control_criticidad; Type: INDEX; Schema: public; Owner: jwalters
--

CREATE INDEX idx_control_criticidad ON public.control_operacional USING btree (nivel_criticidad);


--
-- Name: idx_matriz_area; Type: INDEX; Schema: public; Owner: jwalters
--

CREATE INDEX idx_matriz_area ON public.matriz_aspectos USING btree (area);


--
-- Name: idx_op_facultad; Type: INDEX; Schema: public; Owner: jwalters
--

CREATE INDEX idx_op_facultad ON public.usuario_operativo USING btree (facultad_id);


--
-- Name: idx_residuo_bitacora; Type: INDEX; Schema: public; Owner: jwalters
--

CREATE INDEX idx_residuo_bitacora ON public.residuo USING btree (bitacora_id);


--
-- Name: idx_residuo_tipo; Type: INDEX; Schema: public; Owner: jwalters
--

CREATE INDEX idx_residuo_tipo ON public.residuo USING btree (tipo);


--
-- Name: idx_supervision_area; Type: INDEX; Schema: public; Owner: jwalters
--

CREATE INDEX idx_supervision_area ON public.supervision USING btree (area);


--
-- Name: idx_usuario_rol; Type: INDEX; Schema: public; Owner: jwalters
--

CREATE INDEX idx_usuario_rol ON public.usuario USING btree (rol);


--
-- Name: aspecto_control fk_ac_aspecto; Type: FK CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.aspecto_control
    ADD CONSTRAINT fk_ac_aspecto FOREIGN KEY (aspecto_id) REFERENCES public.aspecto_ambiental(id) ON DELETE CASCADE;


--
-- Name: aspecto_control fk_ac_control; Type: FK CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.aspecto_control
    ADD CONSTRAINT fk_ac_control FOREIGN KEY (control_id) REFERENCES public.control_operacional(id) ON DELETE RESTRICT;


--
-- Name: administrador fk_admin_usuario; Type: FK CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.administrador
    ADD CONSTRAINT fk_admin_usuario FOREIGN KEY (id) REFERENCES public.usuario(id) ON DELETE CASCADE;


--
-- Name: aspecto_ambiental fk_aspecto_matriz; Type: FK CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.aspecto_ambiental
    ADD CONSTRAINT fk_aspecto_matriz FOREIGN KEY (matriz_id) REFERENCES public.matriz_aspectos(id) ON DELETE CASCADE;


--
-- Name: bitacora_residuos fk_bitacora_firma; Type: FK CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.bitacora_residuos
    ADD CONSTRAINT fk_bitacora_firma FOREIGN KEY (firma_id) REFERENCES public.firma_digital(id) ON DELETE RESTRICT;


--
-- Name: bitacora_residuos fk_bitacora_usuario; Type: FK CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.bitacora_residuos
    ADD CONSTRAINT fk_bitacora_usuario FOREIGN KEY (registrado_por) REFERENCES public.usuario(id) ON DELETE RESTRICT;


--
-- Name: item_supervision fk_item_supervision; Type: FK CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.item_supervision
    ADD CONSTRAINT fk_item_supervision FOREIGN KEY (supervision_id) REFERENCES public.supervision(id) ON DELETE CASCADE;


--
-- Name: matriz_aspectos fk_matriz_usuario; Type: FK CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.matriz_aspectos
    ADD CONSTRAINT fk_matriz_usuario FOREIGN KEY (creado_por) REFERENCES public.usuario(id) ON DELETE RESTRICT;


--
-- Name: usuario_operativo fk_op_facultad; Type: FK CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.usuario_operativo
    ADD CONSTRAINT fk_op_facultad FOREIGN KEY (facultad_id) REFERENCES public.facultad(id) ON DELETE RESTRICT;


--
-- Name: usuario_operativo fk_op_usuario; Type: FK CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.usuario_operativo
    ADD CONSTRAINT fk_op_usuario FOREIGN KEY (id) REFERENCES public.usuario(id) ON DELETE CASCADE;


--
-- Name: residuo fk_residuo_bitacora; Type: FK CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.residuo
    ADD CONSTRAINT fk_residuo_bitacora FOREIGN KEY (bitacora_id) REFERENCES public.bitacora_residuos(id) ON DELETE CASCADE;


--
-- Name: supervision fk_sup_usuario; Type: FK CONSTRAINT; Schema: public; Owner: jwalters
--

ALTER TABLE ONLY public.supervision
    ADD CONSTRAINT fk_sup_usuario FOREIGN KEY (supervisor) REFERENCES public.usuario(id) ON DELETE RESTRICT;


--
-- PostgreSQL database dump complete
--

\unrestrict NuV7J4gdxDZTdVPuhur5M30VZf0ePH9uLhpYuf4yDc1T3zv1phz4x5KF8o0ixF2

