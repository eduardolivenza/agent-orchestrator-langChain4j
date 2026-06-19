# Agent Orchestrator — LangChain4j

A multi-agent short-story generator built with **Spring Boot** and **LangChain4j Agentic**. It demonstrates non-sequential orchestration: parallel draft generation, agent-driven selection, and a critic-feedback loop that sends stories back for revision until they meet a quality threshold.

## Architecture

### Agents

| Agent | Role | Tools |
|---|---|---|
| `PlotAgent` | Creates a structured plot outline from a topic | `GenreConventionsTool` |
| `WriterAgent` | Writes a 500–800 word story from a plot | `WordCountTool` |
| `DraftSelectorAgent` | Picks the stronger of two drafts using editorial judgment | — |
| `CriticAgent` | Scores and critiques a story (1–10) | `ReadabilityScoreTool` |
| `EditorAgent` | Rewrites a story to address the critique | `WordCountTool` |

### Orchestration flow

```
[1] PlotAgent(topic)
        │
        ▼
[2] WriterAgent(plot) ──┐   ← two drafts written in parallel
    WriterAgent(plot) ──┘
        │
        ▼
[3] DraftSelectorAgent(draft1, draft2)   ← editorial agent picks the better one
        │
        ▼
[4] CriticAgent(story)   ← initial score
        │
        ├─ score ≥ 7  ──────────────────────────────────────────┐
        │                                                        │
        └─ score < 7  →  EditorAgent(story, critique)           │
                              │                                  │
                              └─ CriticAgent(story)  ──────┐    │
                                   (up to 3 revisions)     │    │
                                   score ≥ 7 or max hit ───┘    │
                                                                 │
[5] EditorAgent(story, critique)   ← final polish (always) ◄────┘
```

The two `WriterAgent` calls run on virtual threads via `CompletableFuture`. All agent invocations are traced via `AgentTracingListener` (timing, token usage) registered on each individual agent builder.

### Guardrails

- **`TopicLengthGuardrail`** (input, `PlotAgent`): rejects topics shorter than 5 or longer than 500 characters.
- **`PlotStructureGuardrail`** (output, `PlotAgent`): verifies the outline contains all required sections; reprompts if any are missing.

## Configuration

All LLM settings live in `src/main/resources/application.yml`:

```yaml
app:
  openai:
    api-key: ${OPENAI_API_KEY}
    endpoint: https://api.openai.com/v1
    model-name: gpt-4o-mini
    max-tokens: 2048
```

The project targets any OpenAI-compatible endpoint — set `endpoint` to point at a local proxy or alternative provider.

## Running

```bash
export OPENAI_API_KEY=sk-...
./gradlew bootRun
```

The API is available at `http://localhost:8080`. Interactive docs (Swagger UI) at `http://localhost:8080/swagger-ui.html`.

### Generate a story

```bash
curl -s -X POST http://localhost:8080/api/stories \
  -H "Content-Type: application/json" \
  -d '{"topic": "A lighthouse keeper who discovers the fog is alive", "genre": "horror"}' \
  | jq .
```

Response:

```json
{
  "topic": "A lighthouse keeper who discovers the fog is alive",
  "story": "...",
  "processingTimeMs": 74321
}
```

## Tech stack

| | |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1 |
| Agent framework | LangChain4j Agentic 1.16.3-beta26 |
| Build | Gradle (Kotlin DSL) |
| API docs | SpringDoc OpenAPI 3 |


## Story

Viernes a las 18:07**\n\nA las 18:07, cuando una oficina empieza a sonar como un acuario vacío, el móvil de Bea vibró sobre la mesa y le cambió la vida.\n\nLa planta 23 estaba medio a oscuras. Las luces automáticas habían renunciado a la mitad de los pasillos, y en las mamparas de cristal flotaban reflejos de escritorios vacíos, tazas manchadas de café y el perfil cansado de Bea Salvatierra. Madrid ardía naranja al otro lado del ventanal. En su pantalla seguía abierto el informe de liquidez. Encima del título, en pequeño, figuraba el nombre de Nicolás Rivas como responsable del documento, aunque las últimas tres versiones las había rehecho ella. En el chat del equipo, todavía brillaba sin respuesta un mensaje suyo de hacía dos horas: **He corregido la conciliación del bloque Asia. Revisad antes de firmar.**\n\nNadie lo había hecho.\n\nEl móvil vibró otra vez. Un número desconocido.\n\n**No abras el informe de liquidez. Te están usando.**\n\nDebajo apareció una foto tomada en ese mismo instante: ella, de espaldas, sola en su mesa.\n\nBea se levantó de golpe. La silla salió rodando.\n\n—¿Quién coño…?\n\nNo había nadie. Solo el zumbido del aire y, al fondo, la luz azul de la impresora dormida.\n\nLlegó otro mensaje.\n\n**Versión 16:40. Luego 17:12. Busca los decimales muertos. —Marea**\n\nLa curiosidad pudo más que el miedo. Bea abrió el histórico de dashboards y comparó versiones. A simple vista eran idénticas. Pero al ampliar, vio las cicatrices: un decimal retocado, una cuenta agrupada bajo otra etiqueta, una regla de conciliación movida unos céntimos. Aislados eran ruido. Juntos, dibujaban una corriente.\n\nLanzó una consulta más profunda y sintió que se le secaba la boca.\n\nPequeños desvíos repartidos entre cuentas pantalla convergían en una transferencia mayor programada para el cierre nocturno. Si el proceso automático corría a medianoche, cientos de clientes quedarían expuestos y el rastro técnico señalaría a la autora del sistema: ella.\n\nOyó pasos.\n\nTomás Vera apareció con un bote de frutos secos en la mano y una sonrisa de viernes. Llevaba la corbata floja y el cansancio mal disimulado de quien llevaba demasiado tiempo obedeciendo.\n\n—¿Sigues aquí? —preguntó—. Pensé que ya te habías ido.\n\nBea bloqueó la pantalla.\n\n—Se me hizo tarde.\n\nTomás se apoyó en la mampara.\n\n—No deberías tocar ese informe sin avisar. Con el protocolo nuevo, saltan alertas raras.\n\nBea lo miró fijo.\n\n—¿Qué protocolo?\n\nTomás pestañeó, apenas un segundo.\n\n—El que pidió Nicolás.\n\nSu móvil vibró otra vez.\n\n**No confíes en Tomás. No llames desde la red corporativa.**\n\nA la vez, en su portátil saltó una alarma roja: **ACCESO RESTRINGIDO. PROTOCOLO DE CONTENCIÓN ACTIVO.** Las puertas de la planta sonaron con un clic seco.\n\nTomás dejó de sonreír.\n\nBea agarró el móvil y salió hacia la escalera. Bajó dos pisos antes de atreverse a respirar. En el rellano del 21 contestó una llamada oculta.\n\n—No hables alto —susurró una mujer—. Soy Irene Lozano.\n\nBea apoyó la espalda en la pared fría.\n\n—Irene, ¿qué pasa?\n\n—Están borrando logs y montando un rastro con tu usuario. Quieren dejarte como responsable. Baja al centro de datos. La copia original de los informes sigue viva en un nodo espejo.\n\nIrene colgó sin despedirse. Ese gesto seco era muy suyo; en auditoría interna la llamaban cirujana porque no levantaba la voz ni pedía permiso antes de cortar.\n\nSe encontraron en el subsuelo, ante una puerta gris con lector biométrico. Irene llevaba la mandíbula tensa, el pelo mal recogido y una tablet llena de mapas de red.\n\n—Dime que no has avisado a nadie —dijo.\n\n—A nadie.\n\n—Bien.\n\nMientras abría la puerta, Bea vio en la tablet ramas de cuentas, horarios de acceso, firmas cruzadas. No necesitó que Irene le explicara demasiado. Lo entendió al mirar el patrón: no buscaban solo sacar dinero. Estaban forzando una apariencia de tensión de caja justo antes del cierre asiático. Si la liquidez consolidada se torcía esa noche, la compra de Kinsale Asia caería por pánico antes de que abriera Tokio. Alguien ganaría mucho con ese derrumbe.\n\nEl centro de datos exhaló aire helado. Filas de racks negros parpadeaban como una ciudad microscópica. Allí, en un terminal abierto con el alias **MAREA**, encontraron a Tomás.\n\nTenía el labio roto y la camisa arrugada, como si ya hubiera perdido una discusión con alguien más fuerte.\n\n—Perdona —dijo al ver a Bea—. Yo envié los mensajes.\n\nIrene no apartó la vista de él.\n\n—Habla.\n\nTomás tragó saliva.\n\n—Nicolás me pidió cambios pequeños. Permisos, ventanas de acceso, cosas feas pero defendibles. Yo creí que era maquillaje contable, el tipo de porquería que luego alguien maquilla mejor en comité. —Miró a Bea—. Cuando vi que no iban a robar solo dinero, sino a hundir la operación y dejarlo todo colgado de tu usuario, ya era tarde. Y sí, también iban a quemarme a mí si algo salía mal.\n\nNo sonó heroico. Sonó peor: verdadero.\n\nA Bea le ardieron las mejillas. Meses de informes rehechos, reuniones donde nadie la miraba, correcciones invisibles, su trabajo firmado por otros. No había sido indiferencia. Habían estado construyendo un cuerpo donde clavar el error.\n\nEn la tablet de Irene apareció una cuenta atrás. Quedaban menos de tres minutos.\n\n—No llegamos a desmontarlo por detrás —dijo ella—. Ya está enganchado a la videollamada de cierre.\n\nBea miró el reloj, luego el terminal, luego su propio nombre incrustado en la trampa. Sintió miedo, sí, pero limpio. Útil.\n\n—Entonces no huimos —dijo—. Lo hacemos delante de todos.\n\nEntró en la videollamada desde el nodo espejo. Nicolás Rivas apareció en una sala de cristal impecable, con consejeros conectados desde Londres y Singapur. Llevaba la serenidad de siempre, esa cortesía pulida con la que se apropiaba de ideas ajenas sin tocar una coma.\n\n—Perdonen la interrupción —dijo Bea—. Voy a enseñarles cómo su director financiero ha manipulado las métricas de liquidez usando un sistema que diseñé yo.\n\nNicolás sonrió con una calma casi ofensiva.\n\n—Bea, estás confundida.\n\n—No. Estoy cansada.\n\nCompartió pantalla. No mostró solo registros: mostró el dibujo completo. Cómo los microajustes fabricaban una falsa tensión de caja. Cómo esa señal activaría cláusulas bancarias y tumbaría la adquisición. Cómo las posiciones externas vinculadas a sociedades pantalla ganarían con el desplome. Fue levantando la estructura pieza a pieza, dejando que cada enlace apareciera delante de todos en lugar de explicarlo de una vez.\n\nLas voces estallaron en la llamada. Irene restauró el nodo original. En una esquina de la pantalla, la transferencia cambió de **PENDIENTE** a **BLOQUEADA**.\n\nNicolás perdió la serenidad por primera vez.\n\n—¿Quién te dio acceso a esto?\n\nBea vio su reflejo diminuto entre las ventanas de la videollamada: una mujer que llevaba meses siendo fondo de pantalla.\n\n—Usted —dijo—. Cada vez que decidió que yo no importaba.\n\nCuando seguridad entró en la sala de Nicolás, Madrid ya era una superficie negra salpicada de luces. Tomás temblaba sentado junto a los servidores. Irene seguía tecleando sin parar, pero con una mueca satisfecha que en ella casi equivalía a una sonrisa.\n\nEl móvil de Bea vibró una última vez.\n\n**18:07 no fue la hora del primer mensaje. Fue la del primer acceso a tu rutina, hace nueve meses. Así te eligieron.**\n\nSin firma.\n\nBea levantó la vista hacia las hileras infinitas de máquinas y sintió el aire frío en la nuca. Por primera vez en mucho tiempo, ya no se sentía invisible.\n\nSolo observada.",
