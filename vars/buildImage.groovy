def call(String imageName, String dockerTag) {
    sh "docker build -t ${imageName}:${dockerTag} ."
    return docker.image("${imageName}:${dockerTag}")
}