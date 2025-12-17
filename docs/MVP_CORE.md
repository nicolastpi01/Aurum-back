# Aurum – MVP Core Bancario

Este documento define el alcance exacto del **MVP Core Bancario** de Aurum.

El MVP se considera **completo** cuando todos los issues listados en este documento están en estado DONE.

---

## 🎯 Objetivo del MVP

Permitir que un usuario:
- se registre y autentique,
- tenga cuentas bancarias,
- consulte saldos y movimientos,
- realice transferencias internas de forma consistente y segura.

---

## 🧩 Alcance funcional incluido

- Autenticación básica (login / registro)
- Gestión de cuentas
- Ledger contable inmutable
- Transferencias internas con idempotencia
- Visualización de movimientos
- Frontend funcional (login + dashboard + transferencias)

---

## 🚫 Fuera de alcance (explícito)

- MFA
- Recupero de contraseña
- Notificaciones
- Panel de administración
- Transferencias externas
- Reversos

---

## 📋 Issues que componen el MVP

### Backend
- [ ] Registro de usuario
- [ ] Login JWT
- [ ] Modelo de cuentas
- [ ] Ledger de movimientos
- [ ] Transferencias internas
- [ ] Idempotencia
- [ ] Listado de movimientos

### Frontend
- [ ] Login
- [ ] Dashboard
- [ ] Transferencias

---

## ✅ Definition of Done (MVP)

Un issue se considera DONE cuando:
- Cumple criterios funcionales
- Incluye tests según su tipo
- No rompe otros módulos
- Pasa build y tests

---

## 🟢 Estado del MVP
- Estado actual: **IN PROGRESS**
- Fecha estimada de cierre: _(a definir)_
