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
import com.project.quizapp.model.Quiz;

@Service
public class QuestionService {
    @Autowired
    QuestionDao questionDao;
    @Autowired
    QuizDao quizDao;
    
    public ResponseEntity<List<Question>> getAllQuestions() {
        try {
            return new ResponseEntity<>(questionDao.findAll(), HttpStatus.OK);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<List<Question>> getQuestionsByCategory(String category) {
        try {
            return new ResponseEntity<>(questionDao.findByCategory(category), HttpStatus.OK);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> addQuestion(Question question) {
        questionDao.save(question);
        return new ResponseEntity<>("Success", HttpStatus.CREATED);
    }

    public ResponseEntity<String> deleteQuestion(Integer id) {
        try {
            Question question = questionDao.findById(id).get();
            List<Integer> quizId = quizDao.findQuizByQuestion(id);
            for(Integer qid: quizId) {
                Quiz quiz = quizDao.findById(qid).get();
                quiz.getQuestion().remove(question);
                quizDao.save(quiz);
            }
            questionDao.delete(question);
            return new ResponseEntity<>("Success", HttpStatus.NO_CONTENT);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Incorrect Question ID provided.", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<String> updateQuestion(Integer id, Question question) {
        Optional<Question> oldQuestion = questionDao.findById(id);
        if(oldQuestion.isPresent()) {
            Question q = oldQuestion.get();
            q.setQuestionTitle(question.getQuestionTitle());
            q.setOption1(question.getOption1());
            q.setOption2(question.getOption2());
            q.setOption3(question.getOption3());
            q.setOption4(question.getOption4());
            q.setRightAnswer(question.getRightAnswer());
            q.setDifficultyLevel(question.getDifficultyLevel());
            q.setCategory(question.getCategory());
            questionDao.save(q);
            return new ResponseEntity<>("Success", HttpStatus.OK);
        }
        else return new ResponseEntity<>("Incorrect Question ID provided.", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<Question> getQuestionById(Integer id) {
        return questionDao.findById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
