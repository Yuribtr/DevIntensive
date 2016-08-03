package com.softdesign.devintensive.data.network.res;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GetUserRes {

    @SerializedName("success")
    @Expose
    public boolean success;
    @SerializedName("data")
    @Expose
    public Data data;

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public Data getData() {
        return data;
    }

    public class Data {

        @SerializedName("_id")
        @Expose
        public String id;
        @SerializedName("first_name")
        @Expose
        public String firstName;
        @SerializedName("second_name")
        @Expose
        public String secondName;
        @SerializedName("__v")
        @Expose
        public int v;
        @SerializedName("repositories")
        @Expose
        public Repositories repositories;
        @SerializedName("contacts")
        @Expose
        public Contacts contacts;
        @SerializedName("profileValues")
        @Expose
        public ProfileValues profileValues;
        @SerializedName("publicInfo")
        @Expose
        public PublicInfo publicInfo;
        @SerializedName("specialization")
        @Expose
        public String specialization;
        @SerializedName("role")
        @Expose
        public String role;
        @SerializedName("updated")
        @Expose
        public String updated;

        public void setId(String id) {
            this.id = id;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public void setSecondName(String secondName) {
            this.secondName = secondName;
        }

        public void setV(int v) {
            this.v = v;
        }

        public void setRepositories(Repositories repositories) {
            this.repositories = repositories;
        }

        public void setContacts(Contacts contacts) {
            this.contacts = contacts;
        }

        public void setProfileValues(ProfileValues profileValues) {
            this.profileValues = profileValues;
        }

        public void setPublicInfo(PublicInfo publicInfo) {
            this.publicInfo = publicInfo;
        }

        public void setSpecialization(String specialization) {
            this.specialization = specialization;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public void setUpdated(String updated) {
            this.updated = updated;
        }

        public String getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getSecondName() {
            return secondName;
        }

        public int getV() {
            return v;
        }

        public Repositories getRepositories() {
            return repositories;
        }

        public Contacts getContacts() {
            return contacts;
        }

        public ProfileValues getProfileValues() {
            return profileValues;
        }

        public PublicInfo getPublicInfo() {
            return publicInfo;
        }

        public String getSpecialization() {
            return specialization;
        }

        public String getRole() {
            return role;
        }

        public String getUpdated() {
            return updated;
        }

        public class Repositories {

            @SerializedName("repo")
            @Expose
            public List<Repo> repo = new ArrayList<Repo>();
            @SerializedName("updated")
            @Expose
            public String updated;

            public void setRepo(List<Repo> repo) {
                this.repo = repo;
            }

            public void setUpdated(String updated) {
                this.updated = updated;
            }

            public List<Repo> getRepo() {
                return repo;
            }

            public String getUpdated() {
                return updated;
            }

            public class Repo {

                @SerializedName("_id")
                @Expose
                public String id;
                @SerializedName("git")
                @Expose
                public String git;
                @SerializedName("title")
                @Expose
                public String title;

                public void setId(String id) {
                    this.id = id;
                }

                public void setGit(String git) {
                    this.git = git;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public String getId() {
                    return id;
                }

                public String getGit() {
                    return git;
                }

                public String getTitle() {
                    return title;
                }
            }

        }

        public class Contacts {

            @SerializedName("vk")
            @Expose
            public String vk;
            @SerializedName("phone")
            @Expose
            public String phone;
            @SerializedName("email")
            @Expose
            public String email;
            @SerializedName("updated")
            @Expose
            public String updated;

            public void setVk(String vk) {
                this.vk = vk;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public void setUpdated(String updated) {
                this.updated = updated;
            }

            public String getVk() {
                return vk;
            }

            public String getPhone() {
                return phone;
            }

            public String getEmail() {
                return email;
            }

            public String getUpdated() {
                return updated;
            }
        }

        public class ProfileValues {

            @SerializedName("homeTask")
            @Expose
            public int homeTask;
            @SerializedName("projects")
            @Expose
            public int projects;
            @SerializedName("linesCode")
            @Expose
            public int linesCode;
            @SerializedName("likesBy")
            @Expose
            public List<String> likesBy = new ArrayList<String>();
            @SerializedName("rait")
            @Expose
            public int rait;
            @SerializedName("updated")
            @Expose
            public String updated;
            @SerializedName("rating")
            @Expose
            public int rating;

            public void setHomeTask(int homeTask) {
                this.homeTask = homeTask;
            }

            public void setProjects(int projects) {
                this.projects = projects;
            }

            public void setLinesCode(int linesCode) {
                this.linesCode = linesCode;
            }

            public void setLikesBy(List<String> likesBy) {
                this.likesBy = likesBy;
            }

            public void setRait(int rait) {
                this.rait = rait;
            }

            public void setUpdated(String updated) {
                this.updated = updated;
            }

            public void setRating(int rating) {
                this.rating = rating;
            }

            public int getHomeTask() {
                return homeTask;
            }

            public int getProjects() {
                return projects;
            }

            public int getLinesCode() {
                return linesCode;
            }

            public List<String> getLikesBy() {
                return likesBy;
            }

            public int getRait() {
                return rait;
            }

            public String getUpdated() {
                return updated;
            }

            public int getRating() {
                return rating;
            }
        }

        public class PublicInfo {

            @SerializedName("bio")
            @Expose
            public String bio;
            @SerializedName("avatar")
            @Expose
            public String avatar;
            @SerializedName("photo")
            @Expose
            public String photo;
            @SerializedName("updated")
            @Expose
            public String updated;

            public void setBio(String bio) {
                this.bio = bio;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public void setPhoto(String photo) {
                this.photo = photo;
            }

            public void setUpdated(String updated) {
                this.updated = updated;
            }

            public String getBio() {
                return bio;
            }

            public String getAvatar() {
                return avatar;
            }

            public String getPhoto() {
                return photo;
            }

            public String getUpdated() {
                return updated;
            }
        }
    }


}
