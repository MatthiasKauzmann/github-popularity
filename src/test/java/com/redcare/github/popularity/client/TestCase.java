package com.redcare.github.popularity.client;

record TestCase(String name, String accessToken, int repoCount,
                int fetchTimesExpected, int pagesExpected) {
}
