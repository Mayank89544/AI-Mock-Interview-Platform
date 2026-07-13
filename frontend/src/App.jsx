import { useState, useEffect } from 'react';
import './App.css';

function App() {
  // Track which view is active: setup, session, or report
  const [view, setView] = useState('setup');
  const [role, setRole] = useState('');
  const [difficulty, setDifficulty] = useState('');
  const [questions, setQuestions] = useState([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answer, setAnswer] = useState('');
  const [evaluations, setEvaluations] = useState([]);
  const [loading, setLoading] = useState(false);

    // State to hold past interview sessions from the database
    const [history, setHistory] = useState([]);

    // Fetch interview history when the component mounts
    useEffect(() => {
      fetchHistory();
    }, []);

    const fetchHistory = async () => {
      try {
        const res = await fetch('http://localhost:8080/api/interview/history');
        const data = await res.json();
        setHistory(data);
      } catch (err) {
        console.log('Could not fetch history');
      }
    };

  // Send role and difficulty to the backend, receive 5 questions
  const startInterview = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await fetch('http://localhost:8080/api/interview/start', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ role, difficulty })
      });
      const data = await res.json();
      setQuestions(data.questions);
      setView('session');
    } catch (err) {
      alert('Error starting interview. Is the backend running?');
    }
    setLoading(false);
  };


  // Send the user's answer to the backend for AI evaluation
  const submitAnswer = async () => {
    if (!answer.trim()) return;
    setLoading(true);
    try {
      const res = await fetch('http://localhost:8080/api/interview/evaluate', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          question: questions[currentIndex].question,
          answer: answer,
          role: role
        })
      });
      const evaluation = await res.json();
      setEvaluations([...evaluations, {
        question: questions[currentIndex].question,
        topic: questions[currentIndex].topic,
        answer: answer,
        ...evaluation
      }]);
      setAnswer('');
      if (currentIndex < questions.length - 1) {
        setCurrentIndex(currentIndex + 1);
      }
        else {
               // Save the completed interview to the database
               const allEvaluations = [...evaluations, {
                 question: questions[currentIndex].question,
                 answer: answer,
                 ...evaluation
               }];
               await fetch('http://localhost:8080/api/interview/save', {
                 method: 'POST',
                 headers: { 'Content-Type': 'application/json' },
                 body: JSON.stringify({
                   role: role,
                   difficulty: difficulty,
                   answers: allEvaluations
                 })
               });
               fetchHistory();
               setView('report');
             }
    } catch (err) {
      alert('Error evaluating answer.');
    }
    setLoading(false);
  };


  // Setup view: role and difficulty selection form
  if (view === 'setup') {
    return (
      <div className="container">
        <h1>AI Mock Interview</h1>
        <p className="subtitle">Practice technical interviews with AI-powered feedback</p>
        <form onSubmit={startInterview} className="setup-form">
          <label>Job Role</label>
          <select value={role} onChange={(e) => setRole(e.target.value)} required>
            <option value="">Select a role...</option>
            <option value="Java Backend Developer">Java Backend Developer</option>
            <option value="Full-Stack Developer">Full-Stack Developer</option>
            <option value="React Frontend Developer">React Frontend Developer</option>
            <option value="Spring Boot Developer">Spring Boot Developer</option>
          </select>
          <label>Difficulty Level</label>
          <select value={difficulty} onChange={(e) => setDifficulty(e.target.value)} required>
            <option value="">Select difficulty...</option>
            <option value="Junior">Junior</option>
            <option value="Mid-Level">Mid-Level</option>
            <option value="Senior">Senior</option>
          </select>
          <button type="submit" disabled={loading}>
            {loading ? 'Generating Questions...' : 'Start Interview'}
          </button>
        </form>
                {history.length > 0 && (
                  <div style={{ marginTop: '40px' }}>
                    <h2 style={{ marginBottom: '16px' }}>Past Interviews</h2>
                    {history.map((session) => (
                      <div key={session.id} className="evaluation-card">
                        <h3>{session.role} - {session.difficulty}</h3>
                        <p style={{ color: '#94a3b8', fontSize: '0.85rem' }}>
                          {new Date(session.completedAt).toLocaleDateString()}
                        </p>
                        <p style={{ color: '#60a5fa', marginTop: '8px' }}>
                          Average Score: {session.averageScore}/10
                        </p>
                      </div>
                    ))}
                  </div>
                )}
      </div>
    );
  }

  // Session view: display one question at a time with answer input
  if (view === 'session') {
    return (
      <div className="container">
        <div className="progress">Question {currentIndex + 1} of {questions.length}</div>
        <div className="topic-badge">{questions[currentIndex].topic}</div>
        <h2 className="question">{questions[currentIndex].question}</h2>
        <textarea
          value={answer}
          onChange={(e) => setAnswer(e.target.value)}
          placeholder="Type your answer here..."
          rows={8}
        />
        <button onClick={submitAnswer} disabled={loading || !answer.trim()}>
          {loading ? 'Evaluating...' : currentIndex < questions.length - 1 ? 'Submit & Next' : 'Submit & Finish'}
        </button>
      </div>
    );
  }

  // Report view: display scores and feedback for all questions
  if (view === 'report') {
    const avgScore = evaluations.length > 0
      ? Math.round(evaluations.reduce((sum, e) => sum + e.overallScore, 0) / evaluations.length)
      : 0;

    return (
      <div className="container">
        <h1>Interview Report</h1>
        <div className="overall-score">
          <span className="score-number">{avgScore}</span>/10
          <p>Overall Performance</p>
        </div>
        {evaluations.map((evaluation , i) => (
          <div key={i} className="evaluation-card">
            <h3>Q{i + 1}: {evaluation .question}</h3>
            <p className="your-answer"><strong>Your answer:</strong> {evaluation .answer}</p>
            <div className="scores">
              <span>Technical: {evaluation .technicalAccuracy}/10</span>
              <span>Depth: {evaluation .depth}/10</span>
              <span>Clarity: {evaluation .clarity}/10</span>
            </div>
            <div className="feedback">
              <p><strong>Strengths:</strong> {evaluation .strengths.join(', ')}</p>
              <p><strong>Improvements:</strong> {evaluation .improvements.join(', ')}</p>
              <p><strong>Tip:</strong> {evaluation .suggestion}</p>
            </div>
          </div>
        ))}
        <button onClick={() => { setView('setup'); setEvaluations([]); setCurrentIndex(0); setQuestions([]); }}>
          Start New Interview
        </button>
      </div>
    );
  }

}


export default App;