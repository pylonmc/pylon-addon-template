package com.balugaq.constructionwand.api.collections;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Pair<A, B> {
    private final A first;
    private final B second;
}
