package org.example.jjava_main.domain.compile;

import lombok.RequiredArgsConstructor;
import org.example.jjava_main.domain.question.QuestionRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CheckService {
    private final QuestionRepository questionRepository;

}
