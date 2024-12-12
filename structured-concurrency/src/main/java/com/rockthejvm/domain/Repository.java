package com.rockthejvm.domain;

import java.net.URI;

public record Repository(String name, Visibility visibility, URI uri) {
}
