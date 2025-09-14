SHELL := /bin/bash
.PHONY: clean build test release tag all bump-patch bump-minor bump-major commit-and-push

# File paths
version_file = version
current_version = 1.0.0
dev_ver = $(current_version)-SNAPSHOT
IMAGE_NAME := ghcr.io/mabuelathato/onboardiq-backend
TAG := $(shell git rev-parse --short HEAD)
PORT := 7000

define bump_version
$(eval MAJOR=$(shell echo $(current_version) | cut -d. -f1))
$(eval MINOR=$(shell echo $(current_version) | cut -d. -f2))
$(eval PATCH=$(shell echo $(current_version) | cut -d. -f3))
endef

# Clean target
clean:
	mvn clean

# Build target (skip tests)
build:
	mvn clean install -DskipTests

# Test target
test-unit-tests:
	@echo "Testing unit tests..."
	mvn -DfailIfNoTests=false test

test-all: test-unit-tests

# Release target
release:
	@echo "Switching to release version..."
	sed -i "s#<version>$(dev_ver)</version>#<version>$(current_version)</version>#" pom.xml
	mvn clean package -DskipTests
	@echo "Reverting to snapshot version..."
	sed -i "s#<version>$(current_version)</version>#<version>$(dev_ver)</version>#" pom.xml
	rm -f libs/ticketsystem-*.jar
	@cp target/ticketsystem-$(current_version)-jar-with-dependencies.jar libs/ticketsystem-$(current_version).jar
	git add libs/ticketsystem-$(current_version).jar
	$(MAKE) tag

# Git tagging
tag:
	git tag -a v$(current_version) -m "Release version $(current_version)"
	git push origin v$(current_version)

# Bump patch version
bump-patch:
	@$(call bump_version)
	@bash -c '\
		MAJOR=$(MAJOR); \
		MINOR=$(MINOR); \
		PATCH=$(PATCH); \
		NEW_VERSION=$$MAJOR.$$MINOR.$$((PATCH + 1)); \
		echo "Bumping patch: $(current_version) → $$NEW_VERSION"; \
		echo $$NEW_VERSION > $(version_file); \
		sed -i "s/<version>$(current_version)-SNAPSHOT<\/version>/<version>$$NEW_VERSION-SNAPSHOT<\/version>/" pom.xml \
	'

# Bump minor version
bump-minor:
	@$(call bump_version)
	@bash -c '\
		MAJOR=$(MAJOR); \
		MINOR=$(MINOR); \
		NEW_VERSION=$$MAJOR.$$((MINOR + 1)).0; \
		echo "Bumping minor: $(current_version) → $$NEW_VERSION"; \
		echo $$NEW_VERSION > $(version_file); \
		sed -i "s/<version>$(current_version)-SNAPSHOT<\/version>/<version>$$NEW_VERSION-SNAPSHOT<\/version>/" pom.xml \
	'

# Bump major version
bump-major:
	@$(call bump_version)
	@bash -c '\
		MAJOR=$(MAJOR); \
		NEW_VERSION=$$((MAJOR + 1)).0.0; \
		echo "Bumping major: $(current_version) → $$NEW_VERSION"; \
		echo $$NEW_VERSION > $(version_file); \
		sed -i "s/<version>$(current_version)-SNAPSHOT<\/version>/<version>$$NEW_VERSION-SNAPSHOT<\/version>/" pom.xml \
	'

commit-and-push:
	@git add .
	@git commit -m "Bump version to $(current_version)"
	@git push origin main

# Docker targets
docker-build:
	docker build --build-arg VERSION=$(current_version) -t $(IMAGE_NAME):$(current_version) .

docker-run:
	docker run -it --rm -p $(PORT):$(PORT) \
		--name onboardiq-server \
		$(IMAGE_NAME):$(current_version)

docker-push:
	docker push $(IMAGE_NAME):$(current_version)

# All targets
all: clean test-all build release

publish-patch: bump-patch release commit-and-push tag

publish-minor: bump-minor release commit-and-push tag

publish-major: bump-major release commit-and-push tag