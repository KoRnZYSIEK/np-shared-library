def call(def image) {
    docker.withRegistry("", "dockerhub") {
        image.push()
        image.push('latest')
    }
}