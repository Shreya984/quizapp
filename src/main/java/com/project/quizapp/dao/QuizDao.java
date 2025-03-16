package com.project.quizapp.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.quizapp.model.Quiz;

public interface QuizDao extends JpaRepository<Quiz, Integer>{
    @Query(value = "SELECT DISTINCT quiz.id FROM quiz, quiz_question WHERE quiz.id = quiz_question.quiz_id AND quiz_question.question_id =:id", nativeQuery = true)
    List<Integer> findQuizByQuestion(Integer id);
}