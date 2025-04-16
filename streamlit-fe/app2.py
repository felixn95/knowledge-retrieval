import requests
import markdown
import re
import streamlit as st

# Default prompts
DEFAULT_PROMPTS = [
    "Der Kunde hat seinen Firmen-Schlüssel verloren. Sind Ansprüche aus beruflichen Tätigkeiten hier abgedeckt?",
    "Wie ist die Deckung auf Mallorca?"
]

policy_number = "P24-1236123"

# Mapping for general domains
GENERAL_DOMAINS = {"Allgemeiner Teil (spartenübergreifend)": "general_contract_terms"}

# Contract and module mappings
CONTRACT_KEYS_MAPPING = {
    "Privathaftpflicht": "privathaftpflicht",
    "Private Haus- und Grundbesitzer": "haus_und_grundbesitzer",
    "Private Gewässerschäden": "gewaesserschaeden",
    "Hundehalter": "hundehalter"
}

MODULE_KEYS_MAPPING = {
    "Dienstrisiko (Privathaftpflicht)": "dienstrisiko",
}

# Configure page
st.set_page_config(page_title="Policy-Agent", layout="wide")

# Custom CSS for a chat-like look
st.markdown("""
<style>
.chat-container {
    display: flex;
    flex-direction: column;
    width: 100%;
    margin-top: 1rem;
}
.chat-bubble-user {
    padding: 1rem;
    border-radius: 10px;
    margin-bottom: 0.5rem;
    max-width: 80%;
    align-self: flex-end;
    background-color: #dcf8c6;
}
.bot-column {
    background-color: #f1f0f0;
    padding: 1rem;
    border-radius: 10px;
    margin-bottom: 0.5rem;
}
</style>
""", unsafe_allow_html=True)

# Centered logo
left_co, cent_co, last_co = st.columns(3)
with cent_co:
    st.image("agent.png")

# Title
st.title("Hi, ich bin dein Vertrags-Agent")
st.subheader(f"Produkt: Bündelpolice Private Haftpflichtversicherung GDV")
st.markdown("##### Wähle die Bereiche, die für deine Abfrage relevant sind:")

# Create a horizontal layout with three columns for each section
col1, col2, col3 = st.columns(3)

with col1:
    st.write("### Verträge")
    selected_contracts = []
    for contract_label, contract_key in CONTRACT_KEYS_MAPPING.items():
        if st.checkbox(contract_label, value=False, key=f"contract_{contract_key}"):
            selected_contracts.append({"contractKey": contract_key})

with col2:
    st.write("### Vertragsbausteine / Zusatzvereinbarungen")
    selected_modules = []
    for module_label, module_key in MODULE_KEYS_MAPPING.items():
        if st.checkbox(module_label, value=False, key=f"module_{module_key}"):
            selected_modules.append({"moduleKey": module_key})

with col3:
    st.write("### Allgemeine Bereiche")
    selected_general_domains = []
    for domain_label, domain_key in GENERAL_DOMAINS.items():
        if st.checkbox(domain_label, value=False, key=f"general_domain_{domain_key}"):
            selected_general_domains.append({"generalDomainKey": domain_key})

st.markdown("---")

# Prompt pills for sample questions
st.write("**Beispielszenarien**:")
cols = st.columns(len(DEFAULT_PROMPTS))
for i, prompt in enumerate(DEFAULT_PROMPTS):
    if cols[i].button(prompt, key=f"prompt_{i}"):
        st.session_state["user_question"] = prompt

# Initialize session state for chat & input
if "chat_history" not in st.session_state:
    st.session_state["chat_history"] = []
if "user_question" not in st.session_state:
    st.session_state["user_question"] = ""

# Check if at least one section is selected
any_selection = selected_contracts or selected_modules or selected_general_domains

if any_selection:
    st.session_state["user_question"] = st.text_input(
        "Stelle eine Frage mit Bezug zu diesem Vertrag oder Modul",
        value=st.session_state["user_question"],
        key="user_input"
    )
else:
    st.info("Bitte wähle mindestens einen Bereich aus, um eine Frage stellen zu können.")
    st.session_state["user_question"] = ""


# Function to display chat history
def display_chat():
    st.markdown('<div class="chat-container">', unsafe_allow_html=True)
    for msg in st.session_state["chat_history"]:
        if msg["role"] == "USER":
            st.markdown(
                f'<div class="chat-bubble-user">{msg["content"]}</div>',
                unsafe_allow_html=True,
            )
        else:
            converted_html = markdown.markdown(msg["content"])
            col1, col2 = st.columns([3, 2])
            with col1:
                st.markdown(
                    f'<div class="bot-column">{converted_html}</div>',
                    unsafe_allow_html=True,
                )
            with col2:
                citations = msg.get("citations", [])
                if citations:
                    st.subheader("Quellen")
                    for i, cit in enumerate(citations, start=1):
                        doc = cit.get("id", "Unbekanntes Dokument")
                        page = cit.get("pageNumber", "–")
                        heading = re.sub(r"^#+\s*", "", cit.get("relatedHeading", "").strip())
                        st.markdown(f"**Q{i}:** {doc}, Seite {page}")
                        if heading:
                            st.markdown(f"Abschnitt: *{heading}*")
    st.markdown("</div>", unsafe_allow_html=True)


# Construct domainsOfInterest for the payload
domains_of_interest = {
    "productKey": "gdv-haftpflicht-privat-01",
    "contracts": selected_contracts,
    "contractModules": selected_modules,
    "generalDomains": selected_general_domains
}

# Ask button logic
if st.button("Ask now"):
    user_question = st.session_state["user_question"].strip()
    if not user_question:
        st.warning("Bitte geben Sie eine Frage ein.")
    else:
        st.session_state["chat_history"].append({"role": "USER", "content": user_question})
        payload = {
            "messages": st.session_state["chat_history"],
            "context": {
                "language": "GERMAN",
                "stream": False,
                "businessKey": policy_number
            },
            "domainsOfInterest": domains_of_interest
        }

        # Debug-Ausgabe
        print("Payload:")
        print(payload)
        print("\n-----------------\n")

        try:
            #response = requests.post("http://localhost:12100/knowledge-retrieval/api/default/query/chat",json=payload)
            response = requests.post("http://quarkus-backend:12100/knowledge-retrieval/api/default/query/chat", json=payload)
            if response.status_code == 200:
                data = response.json()
                print("Response:")
                print(data)
                answer_text = data["responses"][0]["text"][0]["value"]
                citations = data["responses"][0].get("citations", [])

                st.session_state["chat_history"].append({
                    "role": "ASSISTANT",
                    "content": answer_text,
                    "citations": citations
                })
            else:
                st.error(f"Fehler {response.status_code} beim Abrufen der Antwort.")
        except Exception as e:
            st.error(f"Fehler bei der Anfrage: {e}")

# Anzeige der Chat-Historie
display_chat()