package com.project.quizapp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.quizapp.dao.QuestionDao;
import com.project.quizapp.dao.QuizDao;
import com.project.quizapp.model.Question;
import com.project.quizapp.model.QuestionWrapper;
import com.project.quizapp.model.Quiz;
import com.project.quizapp.model.Response;

@Service
public class QuizService {
    
    @Autowired
    QuizDao quizDao;
    @Autowired
    QuestionDao questionDao;

    public ResponseEntity<String> createQuiz(String category, int numQ, String title) {
        List<Question> questions = questionDao.findRandomQuestionsByCategory(category, numQ);
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestion(questions);
        quizDao.save(quiz);

        return new ResponseEntity<>("Success", HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
        Optional<Quiz> quiz = quizDao.findById(id);
        List<Question> questionsFromDB = quiz.get().getQuestion();

        List<QuestionWrapper> questionsForUser = new ArrayList<>();
        for(Question q: questionsFromDB) {
            QuestionWrapper qw = new QuestionWrapper(q.getId(), q.getQuestionTitle(), q.getOption1(), q.getOption2(), q.getOption3(), q.getOption4());
            questionsForUser.add(qw);
        }

        return new ResponseEntity<>(questionsForUser, HttpStatus.OK);
    }

    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
        Quiz quiz = quizDao.findById(id).get();
        if(quiz == null) return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
        List<Question> questions = quiz.getQuestion();
        int right = 0;
        int i = 0;
        for(Response response: responses) {
            if(response.getResponse().equals(questions.get(i).getRightAnswer())) right++;
            i++;
        }
        return new ResponseEntity<>(right, HttpStatus.OK);
    }

    public ResponseEntity<List<Quiz>> getAllQuizzes() {
        try {
            return new ResponseEntity<>(quizDao.findAll(), HttpStatus.FOUND);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<String> deleteQuiz(Integer id) {
        try {
            quizDao.deleteById(id);
            return new ResponseEntity<>("Quiz deleted successfully.", HttpStatus.NO_CONTENT);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Incorrect Quiz ID provided.", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Quiz> getQuiz(Integer id) {
        return quizDao.findById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<String> updateQuiz(Integer id, String title, Integer numQ, String category) {
        try {
            Optional<Quiz> oldQuiz = quizDao.findById(id);
            Quiz q = oldQuiz.get();
            List<Question> questions = questionDao.findRandomQuestionsByCategory(category, numQ);
            q.setTitle(title);
            q.setQuestion(questions);
            quizDao.save(q);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Incorrect Data provided.", HttpStatus.NOT_FOUND);
    }
}
