
import { useState } from 'react'
import './App.css'


function App() {
  const [question, setQuestion] = useState('')
  const [messages, setMessages] = useState([])
  const [loading, setLoading] = useState(false)
  const [isListening, setIsListening] = useState(false)

  const [conversationId] = useState(() => {
    let id = sessionStorage.getItem('airportConversationId');

    if (!id) {
      id = crypto.randomUUID();
      sessionStorage.setItem('airportConversationId', id);
    }

    return id;
  });

  function startListening() {
    const SpeechRecognition =
      window.SpeechRecognition || window.webkitSpeechRecognition

    if (!SpeechRecognition) {
      alert('Speech recognition is not supported in this browser.')
      return
    }

    const recognition = new SpeechRecognition()

    recognition.lang = 'en-US'
    recognition.interimResults = false
    recognition.continuous = false

    recognition.onstart = () => setIsListening(true)

    recognition.onresult = (event) => {
      const transcript = event.results[0][0].transcript
      setQuestion(transcript)
    }

    recognition.onerror = () => {
      alert('Could not recognize speech. Please try again.')
    }

    recognition.onend = () => setIsListening(false)

    recognition.start()
  }


  function speakAnswer(text) {
  if (!('speechSynthesis' in window)) {
    alert('Speech synthesis is not supported in this browser.')
    return
  }

  window.speechSynthesis.cancel()

  const utterance = new SpeechSynthesisUtterance(text)
  utterance.lang = 'en-US'
  utterance.rate = 1

  window.speechSynthesis.speak(utterance)
}


  async function sendMessage(event) {
    event.preventDefault()

    if (!question.trim() || loading) return

    const currentQuestion = question.trim()

    setMessages(previous => [
      ...previous,
      { role: 'user', content: currentQuestion }
    ])

    setQuestion('')
    setLoading(true)

    try {
      const response = await fetch(
        `/api/ai/ask?question=${encodeURIComponent(currentQuestion)}&conversationId=${conversationId}`
      )

      if (!response.ok) {
        throw new Error('Failed to get response from server')
      }

      const answer = await response.text()

      setMessages(previous => [
        ...previous,
        { role: 'assistant', content: answer }
      ])
    } catch (error) {
      setMessages(previous => [
        ...previous,
        {
          role: 'assistant',
          content: 'Sorry, something went wrong. Please try again.'
        }
      ])
    } finally {
      setLoading(false)
    }
  }


const suggestedQuestions = [
  "Which airports in New England are strong candidates for terminal expansion?",
  "Compare LAX and SNA congestion levels.",
  "What percentage of flights from Anchorage are long-haul?",
  "What is the unmet flight demand at SFO and why?"
]


  return (
    <div className="app">
      <header className="header">
        <h1>Airport Investment Intelligence</h1>
        <p>AI-powered airport analysis and investment insights</p>
      </header>

      <main className="chat-container">
        <div className="messages">
          {messages.length === 0 && (

<div className="welcome">
  <h2>Welcome to Airport Intelligence</h2>
  <p>
    Explore airport performance, passenger demand and
    potential investment opportunities using AI.
  </p>

  <div className="suggested-questions">
    {suggestedQuestions.map((item, index) => (
      <button
        key={index}
        type="button"
        onClick={() => {
          setQuestion(item)
        }}
      >
        {item}
      </button>
    ))}
  </div>
</div>

          )}

          {messages.map((message, index) => (
            <div
              key={index}
              className={`message ${message.role}`}
            >
              <strong>
                {message.role === 'user' ? 'You' : 'AI Assistant'}
              </strong>
              <p>{message.content}</p>
              {message.role === 'assistant' && (
                <button
                  type="button"
                  onClick={() => speakAnswer(message.content)}
                >
                  🔊 Listen
                </button>
              )}
            </div>
          ))}

          {loading && (
            <div className="message assistant">
              <strong>AI Assistant</strong>
              <p>Analyzing airport data...</p>
            </div>
          )}
        </div>

        <form className="input-area" onSubmit={sendMessage}>
          <input
            type="text"
            value={question}
            onChange={event => setQuestion(event.target.value)}
            placeholder="Ask about an airport..."
            disabled={loading}
          />

          <button
            type="button"
            onClick={startListening}
            disabled={loading || isListening}
          >
            {isListening ? 'Listening...' : '🎤'}
          </button>

          <button type="submit" disabled={loading || !question.trim()}>
            Send
          </button>
        </form>
      </main>
    </div>
  )
}

export default App
